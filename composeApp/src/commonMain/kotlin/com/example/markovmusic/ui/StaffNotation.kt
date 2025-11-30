package com.example.markovmusic.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.markovmusic.model.Note
import com.example.markovmusic.model.Pitch
import com.example.markovmusic.model.Score
import com.example.markovmusic.ui.theme.AppColors

/**
 * 横スクロール可能な五線譜と音符を描画するComposable
 * @param score 楽譜
 * @param playheadPosition 現在の再生位置（0.0 ~ 1.0）
 * @param generationMode 生成モード（"ランダム" or "マルコフ連鎖"）
 * @param isPlaying 再生中かどうか
 */
@Composable
fun StaffNotation(
    score: Score,
    playheadPosition: Float,
    generationMode: String?,
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    // 1拍あたりの幅（dp）を大きめに設定して、30秒分でも見やすく
    val beatWidthDp = 80.dp
    val beatWidthPx = with(density) { beatWidthDp.toPx() }

    // 総幅を計算（音符の数 × 1拍の幅）
    val totalWidthDp = beatWidthDp * score.totalBeats
    val totalWidthPx = with(density) { totalWidthDp.toPx() }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidthPx = with(density) { maxWidth.toPx() }

        // 自動スクロール: Playheadが画面の中央付近に来るようにスクロール
        LaunchedEffect(playheadPosition, isPlaying) {
            if (isPlaying && playheadPosition > 0f) {
                // Playheadの絶対位置（ピクセル）を計算
                val playheadX = playheadPosition * totalWidthPx

                // 画面中央にPlayheadを配置するためのスクロール位置を計算
                // targetScroll = Playheadの位置 - 画面幅の半分
                val targetScroll = (playheadX - screenWidthPx / 2f)
                    .coerceIn(0f, scrollState.maxValue.toFloat())

                // スクロール位置を更新
                scrollState.scrollTo(targetScroll.toInt())
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            Canvas(
                modifier = Modifier
                    .width(totalWidthDp)
                    .fillMaxHeight()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // 五線譜の描画エリア
                val staffTop = canvasHeight * 0.3f
                val staffHeight = canvasHeight * 0.4f
                val lineSpacing = staffHeight / 8f

                // 五線譜を描画
                drawStaffLines(staffTop, lineSpacing)

                // 音符を描画（メロディのみ）
                val noteSize = (lineSpacing * 1.2f).coerceAtMost(beatWidthPx * 0.5f)

                score.notes.forEachIndexed { index, note ->
                    val x = note.startTime * beatWidthPx
                    val noteColor = getNoteColor(generationMode, index)

                    // メロディ音（最も高い音）のみを描画
                    val melodyPitch = note.melodyPitch
                    val staffPos = melodyPitch.midi - Pitch.C4.midi
                    val y = calculateNoteYFromStaffPos(staffPos, staffTop, lineSpacing)
                    val stemUp = shouldStemUp(staffPos)

                    drawMusicalNote(
                        center = Offset(x + beatWidthPx / 2f, y),
                        size = noteSize,
                        color = noteColor,
                        stemUp = stemUp
                    )
                }

                // コード名を描画（五線譜の下）
                drawChordNames(score, beatWidthPx, staffTop, staffHeight, lineSpacing)

                // Playheadを描画（ネオングリーンの縦線）
                val playheadX = playheadPosition * canvasWidth
                drawPlayhead(playheadX, staffTop, staffHeight)
            }
        }
    }
}

/**
 * モードに応じた音符の色を取得
 */
private fun getNoteColor(generationMode: String?, index: Int): Color {
    return when (generationMode) {
        "ランダム" -> {
            // ランダムモード：落ち着いたグレー系をランダムに選択
            AppColors.NoteColors.randomNoteColors[index % AppColors.NoteColors.randomNoteColors.size]
        }
        "マルコフ連鎖" -> {
            // マルコフ連鎖モード：ビビッドなピンク/オレンジ/パープルを交互に
            when (index % 3) {
                0 -> AppColors.NoteColors.MarkovPink
                1 -> AppColors.NoteColors.MarkovOrange
                else -> AppColors.NoteColors.MarkovPurple
            }
        }
        else -> Color.Gray
    }
}

/**
 * 五線譜の5本の線を描画
 */
private fun DrawScope.drawStaffLines(staffTop: Float, lineSpacing: Float) {
    val staffColor = AppColors.StaffLine
    val lineThickness = 2.5f

    for (i in 0..4) {
        val y = staffTop + lineSpacing * (i + 1)
        drawLine(
            color = staffColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = lineThickness
        )
    }
}

/**
 * 音符のY座標を計算
 * ト音記号の標準配置：第3線（真ん中の線）をB4（シ）に対応させる
 * 1音階上がるごとにlineSpacing / 2ずつ上に移動
 */
private fun calculateNoteY(note: Note, staffTop: Float, lineSpacing: Float): Float {
    val thirdLineY = staffTop + lineSpacing * 3 // 第3線（ト音記号のB4）
    val b4Position = 11 // B4のstaffPosition (B4.midi - C4.midi = 71 - 60)
    val pitchOffset = (note.staffPosition - b4Position) * (lineSpacing / 2f)
    return thirdLineY - pitchOffset
}

/**
 * staffPositionからY座標を計算
 * ト音記号の標準配置：第3線（真ん中の線）をB4（シ）に対応させる
 */
private fun calculateNoteYFromStaffPos(staffPosition: Int, staffTop: Float, lineSpacing: Float): Float {
    val thirdLineY = staffTop + lineSpacing * 3 // 第3線（ト音記号のB4）
    val b4Position = 11 // B4のstaffPosition (B4.midi - C4.midi = 71 - 60)
    val pitchOffset = (staffPosition - b4Position) * (lineSpacing / 2f)
    return thirdLineY - pitchOffset
}

/**
 * コード名を描画（五線譜の下）
 */
private fun DrawScope.drawChordNames(
    score: Score,
    beatWidthPx: Float,
    staffTop: Float,
    staffHeight: Float,
    lineSpacing: Float
) {
    val chordY = staffTop + staffHeight + lineSpacing * 2 // 五線譜の下
    val textColor = AppColors.TextPrimary
    val textSize = lineSpacing * 1.5f

    // 4拍ごとにコード名を描画
    var currentChord: String? = null
    score.notes.forEachIndexed { index, note ->
        // 4拍の最初の音符でコード名を描画
        if (index % 4 == 0 && note.chordName != null) {
            currentChord = note.chordName
        }

        if (index % 4 == 0 && currentChord != null) {
            val x = note.startTime * beatWidthPx

            // プラットフォーム固有のテキスト描画を使用
            drawTextOnCanvas(
                text = currentChord!!,
                x = x + beatWidthPx * 2,
                y = chordY,
                textSize = textSize,
                color = textColor
            )
        }
    }
}

/**
 * Playhead（再生位置を示す縦線）を描画
 * ネオングリーンの目立つ線
 */
private fun DrawScope.drawPlayhead(x: Float, staffTop: Float, staffHeight: Float) {
    val playheadColor = AppColors.Playhead
    val lineThickness = 4f

    // メインの線
    drawLine(
        color = playheadColor,
        start = Offset(x, staffTop - 10f),
        end = Offset(x, staffTop + staffHeight + 30f),
        strokeWidth = lineThickness
    )

    // グロー効果（薄い線を重ねる）
    drawLine(
        color = playheadColor.copy(alpha = 0.3f),
        start = Offset(x, staffTop - 10f),
        end = Offset(x, staffTop + staffHeight + 30f),
        strokeWidth = lineThickness * 2.5f
    )
}
