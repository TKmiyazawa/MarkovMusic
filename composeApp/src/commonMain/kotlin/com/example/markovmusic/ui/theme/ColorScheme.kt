package com.example.markovmusic.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ポップで明るい配色テーマ
 * ターゲット：中学3年生向けの親しみやすいデザイン
 */
object AppColors {
    // 背景色
    val Background = Color(0xFFFFFDD0)  // クリーム色
    val SurfaceLight = Color(0xFFFFF8E1) // 薄いレモンイエロー
    val SurfacePastel = Color(0xFFFFE4E1) // パステルピンク

    // 五線譜
    val StaffLine = Color(0xFF2C3E50) // ダークブルーグレー

    // 音符の色
    object NoteColors {
        // ランダムモード用（落ち着いた色のバリエーション）
        val Random1 = Color(0xFF95A5A6) // グレー
        val Random2 = Color(0xFF7F8C8D) // ダークグレー
        val Random3 = Color(0xFFBDC3C7) // ライトグレー

        // マルコフ連鎖モード用（ビビッドで美しい色）
        val MarkovPink = Color(0xFFFF1493) // ビビッドピンク（DeepPink）
        val MarkovOrange = Color(0xFFFF6B35) // ビビッドオレンジ
        val MarkovPurple = Color(0xFFAA4FFF) // ビビッドパープル

        // Gemini AIモード用（宇宙・未来的な色）
        val GeminiBlue = Color(0xFF4285F4) // Googleブルー
        val GeminiCyan = Color(0xFF00D4FF) // サイバーシアン
        val GeminiMagenta = Color(0xFFE040FB) // ネオンマゼンタ

        // ランダムモードの音符をランダムに選ぶためのリスト
        val randomNoteColors = listOf(Random1, Random2, Random3)

        // Geminiモードの音符の色リスト
        val geminiNoteColors = listOf(GeminiBlue, GeminiCyan, GeminiMagenta)
    }

    // Playhead（再生バー）
    val Playhead = Color(0xFF00FFA3) // ネオングリーン（ライムグリーン）
    val PlayheadGlow = Color(0xFF00FFD1) // シアン寄りのグロー

    // ボタン色（キャンディカラー）
    object ButtonColors {
        val Primary = Color(0xFFFF69B4) // ホットピンク
        val Secondary = Color(0xFF87CEEB) // スカイブルー
        val Markov = Color(0xFFFF6B9D) // ピンク系
        val Random = Color(0xFFFFB347) // オレンジ系
        val Gemini = Color(0xFF4285F4) // Googleブルー（Gemini AI用）
        val Play = Color(0xFF4ECDC4) // ターコイズ
        val Stop = Color(0xFFFF6B6B) // コーラルレッド
    }

    // テキスト色
    val TextPrimary = Color(0xFF2C3E50) // ダークブルーグレー
    val TextSecondary = Color(0xFF7F8C8D) // ミディアムグレー
    val TextOnButton = Color.White

    // カード背景
    val CardBackground = Color(0xFFFFFAFA) // スノーホワイト（わずかに暖色）
    val CardAccent = Color(0xFFFFE4E1) // パステルピンク
}
