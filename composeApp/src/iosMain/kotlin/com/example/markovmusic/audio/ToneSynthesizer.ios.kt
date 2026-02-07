package com.example.markovmusic.audio

import com.example.markovmusic.model.Pitch
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.set
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioFormat
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioPlayerNode
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import kotlin.math.PI
import kotlin.math.sin

/**
 * iOS用の音声合成実装
 * AVAudioEngineを使用して正弦波を生成
 */
@OptIn(ExperimentalForeignApi::class)
actual class ToneSynthesizer {
    private var audioEngine: AVAudioEngine? = null
    private var playerNode: AVAudioPlayerNode? = null
    private val sampleRate: Double = 44100.0

    actual fun initialize() {
        try {
            // オーディオセッションを設定
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, null)
            session.setActive(true, null)

            // オーディオエンジンを初期化
            audioEngine = AVAudioEngine()
            playerNode = AVAudioPlayerNode()

            audioEngine?.let { engine ->
                playerNode?.let { player ->
                    engine.attachNode(player)

                    // フォーマットを設定（モノラル、44100Hz）
                    val format = AVAudioFormat(sampleRate, 1u)
                    if (format != null) {
                        engine.connect(player, engine.mainMixerNode, format)
                    }

                    // エンジンを開始
                    engine.prepare()
                    engine.startAndReturnError(null)
                    player.play()
                }
            }
        } catch (e: Exception) {
            println("ToneSynthesizer initialization error: ${e.message}")
        }
    }

    actual fun playTone(pitch: Pitch, durationMs: Long) {
        playChord(listOf(pitch), durationMs)
    }

    actual fun playChord(pitches: List<Pitch>, durationMs: Long) {
        if (pitches.isEmpty()) return

        try {
            val format = AVAudioFormat(sampleRate, 1u) ?: return
            val frameCount = (durationMs * sampleRate / 1000.0).toUInt()

            val buffer = AVAudioPCMBuffer(format, frameCount) ?: return
            buffer.frameLength = frameCount

            val floatChannelData = buffer.floatChannelData ?: return
            val channelData = floatChannelData[0] ?: return

            // 各周波数の正弦波を生成して合成
            for (i in 0 until frameCount.toInt()) {
                var sample = 0.0f
                for (pitch in pitches) {
                    val frequency = pitch.frequency
                    val angle = 2.0 * PI * i * frequency / sampleRate
                    sample += (sin(angle) * 0.5 / pitches.size).toFloat()
                }
                channelData[i] = sample
            }

            // エンベロープを適用
            applyEnvelope(buffer, frameCount.toInt())

            // バッファを再生
            playerNode?.scheduleBuffer(buffer, null)

        } catch (e: Exception) {
            println("ToneSynthesizer playChord error: ${e.message}")
        }
    }

    /**
     * 音の立ち上がりと減衰を滑らかにするエンベロープを適用
     */
    private fun applyEnvelope(buffer: AVAudioPCMBuffer, frameCount: Int) {
        val floatChannelData = buffer.floatChannelData ?: return
        val channelData = floatChannelData[0] ?: return

        val attackSamples = (sampleRate * 0.01).toInt() // 10ms attack
        val releaseSamples = (sampleRate * 0.05).toInt() // 50ms release

        // Attack（立ち上がり）
        for (i in 0 until attackSamples.coerceAtMost(frameCount)) {
            val gain = i.toFloat() / attackSamples
            channelData[i] = channelData[i] * gain
        }

        // Release（減衰）
        val releaseStart = (frameCount - releaseSamples).coerceAtLeast(0)
        for (i in releaseStart until frameCount) {
            val gain = 1f - (i - releaseStart).toFloat() / releaseSamples
            channelData[i] = channelData[i] * gain
        }
    }

    actual fun release() {
        try {
            playerNode?.stop()
            audioEngine?.stop()
            playerNode?.let { audioEngine?.detachNode(it) }
            audioEngine = null
            playerNode = null
        } catch (e: Exception) {
            println("ToneSynthesizer release error: ${e.message}")
        }
    }
}
