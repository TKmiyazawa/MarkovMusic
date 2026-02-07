package com.example.markovmusic.model

/**
 * 楽譜全体を表すデータクラス
 * @param notes 音符のリスト
 * @param bpm テンポ（Beats Per Minute）
 * @param totalBeats 総拍数
 */
data class Score(
    val notes: List<Note>,
    val bpm: Int = 120,
    val totalBeats: Float = 60f  // BPM 120で30秒 = 60拍
) {
    /**
     * 1拍の長さ（ミリ秒）
     */
    val beatDurationMs: Long
        get() = (60_000L / bpm)

    /**
     * 総演奏時間（ミリ秒）
     */
    val totalDurationMs: Long
        get() = (totalBeats * beatDurationMs).toLong()

    companion object {
        /**
         * 指定された秒数に必要な拍数を計算
         * @param durationSeconds 目標の秒数
         * @param bpm テンポ
         * @return 必要な拍数
         */
        fun calculateBeatsForDuration(durationSeconds: Int, bpm: Int): Int {
            // 1拍の長さ（秒） = 60 / bpm
            // 必要な拍数 = 目標秒数 / 1拍の長さ
            return (durationSeconds * bpm / 60)
        }
    }
}
