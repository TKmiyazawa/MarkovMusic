package com.example.markovmusic

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

/**
 * Gemini APIキー取得（iOS版は空文字を返す - iOSではGemini非対応）
 */
actual fun getGeminiApiKey(): String = ""