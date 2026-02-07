package com.example.markovmusic

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

/**
 * Gemini APIキー取得（iOS版）
 * Info.plistからGEMINI_API_KEYを読み取る
 */
actual fun getGeminiApiKey(): String {
    // Info.plistからAPIキーを取得
    val apiKey = NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String
    return apiKey ?: ""
}
