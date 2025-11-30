package com.example.markovmusic.audio

import com.example.markovmusic.model.Pitch

/**
 * 音声合成のインターフェース
 * プラットフォーム固有の実装を提供するためのexpect宣言
 */
expect class ToneSynthesizer() {
    /**
     * 初期化処理
     */
    fun initialize()

    /**
     * 指定された周波数の音を再生
     * @param pitch 音高
     * @param durationMs 再生時間（ミリ秒）
     */
    fun playTone(pitch: Pitch, durationMs: Long)

    /**
     * 和音（複数の音高）を同時に再生
     * @param pitches 音高のリスト
     * @param durationMs 再生時間（ミリ秒）
     */
    fun playChord(pitches: List<Pitch>, durationMs: Long)

    /**
     * リソースの解放
     */
    fun release()
}
