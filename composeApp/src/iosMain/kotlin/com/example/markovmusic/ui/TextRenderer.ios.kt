package com.example.markovmusic.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine

/**
 * iOS用のテキスト描画実装
 * Skiaを使用してテキストを描画
 */
actual fun DrawScope.drawTextOnCanvas(
    text: String,
    x: Float,
    y: Float,
    textSize: Float,
    color: Color
) {
    val skiaCanvas = drawContext.canvas.nativeCanvas

    val paint = Paint().apply {
        this.color = org.jetbrains.skia.Color.makeARGB(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
        isAntiAlias = true
    }

    // シンプルなフォント（システムデフォルト）
    val font = Font().apply {
        size = textSize
    }

    // テキストの幅を取得して中央揃え
    val textLine = TextLine.make(text, font)
    val textWidth = textLine.width

    skiaCanvas.drawTextLine(
        textLine,
        x - textWidth / 2,  // 中央揃え
        y,
        paint
    )
}
