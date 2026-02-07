package com.example.markovmusic.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.markovmusic.audio.ToneSynthesizer
import com.example.markovmusic.model.Note
import com.example.markovmusic.model.Score
import kotlinx.coroutines.delay

/**
 * 楽譜の再生を制御するクラス
 */
class PlaybackController(
    private val score: Score,
    private val synthesizer: ToneSynthesizer
) {
    private val playedNoteIndices = mutableSetOf<Int>()

    /**
     * 現在の再生位置（拍数）を計算
     */
    fun getCurrentBeat(playheadPosition: Float): Float {
        return playheadPosition * score.totalBeats
    }

    /**
     * 再生位置に基づいて音符をトリガー
     */
    fun triggerNotesAtPosition(currentBeat: Float) {
        score.notes.forEachIndexed { index, note ->
            // まだ再生していない音符で、現在の位置を超えた音符を再生
            if (!playedNoteIndices.contains(index) && currentBeat >= note.startTime) {
                playedNoteIndices.add(index)
                // 和音対応: pitchesリストを使用
                synthesizer.playChord(note.pitches, score.beatDurationMs)
            }
        }
    }

    /**
     * 再生状態をリセット
     */
    fun reset() {
        playedNoteIndices.clear()
    }
}

/**
 * 再生アニメーションを管理するComposable
 */
@Composable
fun rememberPlaybackState(
    score: Score,
    synthesizer: ToneSynthesizer,
    isPlaying: Boolean
): PlaybackState {
    var playheadPosition by remember { mutableStateOf(0f) }
    val animatable = remember { Animatable(0f) }
    val controller = remember(score) { PlaybackController(score, synthesizer) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            controller.reset()
            animatable.snapTo(0f)

            // アニメーション開始
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = score.totalDurationMs.toInt(),
                    easing = LinearEasing
                )
            )
        } else {
            animatable.stop()
        }
    }

    // アニメーション中に音符をトリガー
    LaunchedEffect(animatable.value) {
        playheadPosition = animatable.value
        val currentBeat = controller.getCurrentBeat(playheadPosition)
        controller.triggerNotesAtPosition(currentBeat)
    }

    return PlaybackState(
        playheadPosition = playheadPosition,
        isPlaying = isPlaying && animatable.isRunning
    )
}

/**
 * 再生状態を保持するデータクラス
 */
data class PlaybackState(
    val playheadPosition: Float,
    val isPlaying: Boolean
)
