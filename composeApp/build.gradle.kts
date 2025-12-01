import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.google.generativeai)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.markovmusic"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.markovmusic"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        // Gemini API Key from local.properties
        val localProperties = gradleLocalProperties(rootDir, providers)
        val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Task to update iOS Config.xcconfig with GEMINI_API_KEY from local.properties
val updateIosGeminiApiKey by tasks.registering {
    val localPropsFile = rootDir.resolve("local.properties")
    val configFile = rootDir.resolve("iosApp/Configuration/Config.xcconfig")

    inputs.file(localPropsFile).optional()
    outputs.file(configFile)

    doLast {
        val geminiApiKey = if (localPropsFile.exists()) {
            Properties().apply {
                localPropsFile.inputStream().use { load(it) }
            }.getProperty("GEMINI_API_KEY") ?: ""
        } else ""

        if (configFile.exists()) {
            val content = configFile.readText()
            val updatedContent = content.replace(
                Regex("GEMINI_API_KEY=.*"),
                "GEMINI_API_KEY=$geminiApiKey"
            )
            configFile.writeText(updatedContent)
            println("Updated iOS Config.xcconfig with GEMINI_API_KEY")
        } else {
            println("Warning: Config.xcconfig not found at ${configFile.absolutePath}")
        }
    }
}

// Run updateIosGeminiApiKey before iOS framework tasks
tasks.matching { it.name.contains("compileKotlinIos") }.configureEach {
    dependsOn(updateIosGeminiApiKey)
}

