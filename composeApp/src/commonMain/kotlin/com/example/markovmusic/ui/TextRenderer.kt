package com.example.markovmusic.ui

import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * プラットフォーム固有のテキスト描画
 */
expect fun DrawScope.drawTextOnCanvas(
    text: String,
    x: Float,
    y: Float,
    textSize: Float,
    color: androidx.compose.ui.graphics.Color
)
