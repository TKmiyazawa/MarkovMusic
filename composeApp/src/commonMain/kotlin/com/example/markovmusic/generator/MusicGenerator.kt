package com.example.markovmusic.generator

import com.example.markovmusic.model.Score

/**
 * 音楽生成のインターフェース
 */
interface MusicGenerator {
    /**
     * 楽譜を生成する
     * @param numNotes 生成する音符の数（デフォルト: 60 = BPM120で30秒）
     * @return 生成された楽譜
     */
    fun generate(numNotes: Int = 60): Score
}
