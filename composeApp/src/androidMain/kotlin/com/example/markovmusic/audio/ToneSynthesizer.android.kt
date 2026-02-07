package com.example.markovmusic.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.markovmusic.model.Pitch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Android用の音声合成実装
 * AudioTrackを使用して正弦波を生成
 */
actual class ToneSynthesizer {
    private var audioTrack: AudioTrack? = null
    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_OUT_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    actual fun initialize() {
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(audioFormat)
                    .setSampleRate(sampleRate)
                    .setChannelMask(channelConfig)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
    }

    actual fun playTone(pitch: Pitch, durationMs: Long) {
        playChord(listOf(pitch), durationMs)
    }

    actual fun playChord(pitches: List<Pitch>, durationMs: Long) {
        if (pitches.isEmpty()) return

        val numSamples = (durationMs * sampleRate / 1000).toInt()
        val samples = ShortArray(numSamples)

        // 各周波数の正弦波を生成して合成
        for (pitch in pitches) {
            val frequency = pitch.frequency
            for (i in samples.indices) {
                val angle = 2.0 * PI * i * frequency / sampleRate
                val sampleValue = sin(angle) * Short.MAX_VALUE * 0.5 / pitches.size
                samples[i] = (samples[i] + sampleValue.toInt()).toShort()
            }
        }

        // エンベロープを適用（フェードアウト）
        applyEnvelope(samples, durationMs)

        // AudioTrackに書き込み
        audioTrack?.write(samples, 0, samples.size)
    }

    /**
     * 音の立ち上がりと減衰を滑らかにするエンベロープを適用
     */
    private fun applyEnvelope(samples: ShortArray, durationMs: Long) {
        val attackSamples = (sampleRate * 0.01).toInt() // 10ms attack
        val releaseSamples = (sampleRate * 0.05).toInt() // 50ms release

        // Attack（立ち上がり）
        for (i in 0 until attackSamples.coerceAtMost(samples.size)) {
            val gain = i.toFloat() / attackSamples
            samples[i] = (samples[i] * gain).toInt().toShort()
        }

        // Release（減衰）
        val releaseStart = (samples.size - releaseSamples).coerceAtLeast(0)
        for (i in releaseStart until samples.size) {
            val gain = 1f - (i - releaseStart).toFloat() / releaseSamples
            samples[i] = (samples[i] * gain).toInt().toShort()
        }
    }

    actual fun release() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }
}
