# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MarkovMusic is a Kotlin Multiplatform project targeting Android and iOS using Compose Multiplatform. The project uses a shared codebase for UI and business logic with platform-specific implementations where needed.

## Build Commands

### Android
```bash
# Build debug APK
./gradlew :composeApp:assembleDebug

# Build release APK
./gradlew :composeApp:assembleRelease

# Run tests
./gradlew :composeApp:testDebugUnitTest

# Clean build
./gradlew clean
```

### iOS
For iOS builds, open the `/iosApp` directory in Xcode and build from there, or use the IDE's run configuration.

## Architecture

### Multiplatform Structure
- **composeApp/src/commonMain**: Shared code for all platforms including UI components and business logic
- **composeApp/src/androidMain**: Android-specific implementations (MainActivity, platform-specific APIs)
- **composeApp/src/iosMain**: iOS-specific implementations (MainViewController, platform-specific APIs)
- **composeApp/src/commonTest**: Shared test code
- **iosApp**: Native iOS app entry point and SwiftUI integration

### Source Set Dependencies
The project uses Kotlin Multiplatform's source set model:
- `commonMain` contains shared code that compiles for all targets
- Platform-specific folders (`androidMain`, `iosMain`) provide actual implementations of expect/actual declarations
- Platform code can access platform-specific APIs (e.g., Android SDK, iOS CoreCrypto)

### Key Configuration Files
- **gradle/libs.versions.toml**: Centralized version catalog for all dependencies
- **composeApp/build.gradle.kts**: Main module configuration with platform targets and dependencies
- **settings.gradle.kts**: Project structure and repository configuration

### Platform Integration
- **Android**: Uses `MainActivity` extending `ComponentActivity` with `setContent { App() }` to launch the Compose UI
- **iOS**: Framework is generated with `baseName = "ComposeApp"` and `isStatic = true`, consumed by the native iOS app in `/iosApp`

### Compilation Targets
- Android: JVM 11 target with minSdk 24, targetSdk 36
- iOS: `iosArm64` (devices) and `iosSimulatorArm64` (simulator)

## Dependencies
The project uses the version catalog pattern. To add dependencies:
1. Add version to `gradle/libs.versions.toml` under `[versions]`
2. Add library reference under `[libraries]`
3. Reference in `composeApp/build.gradle.kts` using `libs.` prefix in the appropriate source set

## Resource Handling
Compose Multiplatform resources are located in `composeApp/src/commonMain/composeResources/` and accessed via generated `Res` class (e.g., `Res.drawable.compose_multiplatform`).
