package com.example.markovmusic.generator

import com.example.markovmusic.model.Note
import com.example.markovmusic.model.Pitch
import com.example.markovmusic.model.Score
import kotlin.random.Random

/**
 * ランダムに音符を生成するジェネレータ
 * 教育用：「カオスな音楽」を体験するため
 * 不協和音（ディスソナント）を生成して「気持ち悪い音」を強調
 */
class RandomGenerator(private val random: Random = Random.Default) : MusicGenerator {

    override fun generate(numNotes: Int): Score {
        val allPitches = Pitch.values().toList()
        val notes = mutableListOf<Note>()

        repeat(numNotes) { index ->
            // 不協和音を生成：ランダムに2-3音を選択
            val numVoices = random.nextInt(2, 4) // 2または3音
            val chordPitches = List(numVoices) {
                allPitches.random(random)
            }.distinct() // 重複を除去

            val note = Note(
                pitches = chordPitches,
                startTime = index.toFloat(),
                duration = 1f,
                chordName = null // ランダムモードはコード理論なし
            )
            notes.add(note)
        }

        return Score(
            notes = notes,
            bpm = 120,
            totalBeats = numNotes.toFloat()
        )
    }
}
