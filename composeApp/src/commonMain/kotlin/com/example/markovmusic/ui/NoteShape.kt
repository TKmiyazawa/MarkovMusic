package com.example.markovmusic.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate

/**
 * 音符（♩）を描画する関数
 * 符頭（Note Head）と符幹（Stem）を組み合わせて描画
 *
 * @param center 音符の中心位置
 * @param size 音符のサイズ
 * @param color 音符の色
 * @param stemUp 符幹を上向きにするか（true = 上向き、false = 下向き）
 */
fun DrawScope.drawMusicalNote(
    center: Offset,
    size: Float,
    color: Color,
    stemUp: Boolean = true
) {
    val headWidth = size * 0.8f
    val headHeight = size * 0.6f
    val stemThickness = size * 0.12f
    val stemHeight = size * 2.5f

    // 符頭の位置（楕円形）
    val headCenter = if (stemUp) {
        Offset(center.x, center.y)
    } else {
        Offset(center.x, center.y)
    }

    // 符頭を描画（楕円を回転させて音符らしい形に）
    rotate(degrees = -20f, pivot = headCenter) {
        drawOval(
            color = color,
            topLeft = Offset(
                headCenter.x - headWidth / 2,
                headCenter.y - headHeight / 2
            ),
            size = Size(headWidth, headHeight)
        )
    }

    // 符幹を描画
    if (stemUp) {
        // 上向き符幹（符頭の右端から上に伸びる）
        val stemStart = Offset(
            headCenter.x + headWidth / 2 - stemThickness / 2,
            headCenter.y
        )
        val stemEnd = Offset(
            stemStart.x,
            stemStart.y - stemHeight
        )
        drawLine(
            color = color,
            start = stemStart,
            end = stemEnd,
            strokeWidth = stemThickness
        )
    } else {
        // 下向き符幹（符頭の左端から下に伸びる）
        val stemStart = Offset(
            headCenter.x - headWidth / 2 + stemThickness / 2,
            headCenter.y
        )
        val stemEnd = Offset(
            stemStart.x,
            stemStart.y + stemHeight
        )
        drawLine(
            color = color,
            start = stemStart,
            end = stemEnd,
            strokeWidth = stemThickness
        )
    }
}

/**
 * ピッチに基づいて符幹の向きを決定
 * 五線譜の中央線（B4あたり）より上の音は下向き、下の音は上向き
 *
 * @param staffPosition 五線譜上の位置（0が中央線）
 * @return true = 上向き、false = 下向き
 */
fun shouldStemUp(staffPosition: Int): Boolean {
    // staffPositionが7以上（高い音）の場合は下向き
    return staffPosition < 7
}
