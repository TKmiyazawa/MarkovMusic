package com.example.markovmusic

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

/**
 * Gemini APIキーをBuildConfigから取得
 */
actual fun getGeminiApiKey(): String = BuildConfig.GEMINI_API_KEY