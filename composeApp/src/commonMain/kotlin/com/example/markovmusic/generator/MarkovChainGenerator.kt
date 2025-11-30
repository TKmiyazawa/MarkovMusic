package com.example.markovmusic.generator

import com.example.markovmusic.model.Chord
import com.example.markovmusic.model.Note
import com.example.markovmusic.model.Pitch
import com.example.markovmusic.model.Score
import kotlin.random.Random

/**
 * マルコフ連鎖を使用した音楽生成
 * カノン進行（D -> A -> Bm -> F#m -> G -> D -> G -> A）に基づいて、
 * メロディと和音を同時に生成する
 */
class MarkovChainGenerator(private val random: Random = Random.Default) : MusicGenerator {

    override fun generate(numNotes: Int): Score {
        val notes = mutableListOf<Note>()
        val progression = Chord.CANON_PROGRESSION

        repeat(numNotes) { index ->
            // 現在のコードを決定（4拍ごとにコード変更）
            val chordIndex = (index / 4) % progression.size
            val currentChord = progression[chordIndex]

            // マルコフ連鎖：現在のコードの確率分布に基づいてメロディ音を選択
            val melodyPitch = selectPitchByWeight(currentChord.weights)

            // 和音を構成する音のリストを作成
            val chordPitches = mutableListOf<Pitch>()

            // コードの構成音を追加（ルート、第3音、第5音）
            chordPitches.addAll(currentChord.notes)

            // メロディ音を追加（重複していなければ）
            if (!chordPitches.contains(melodyPitch)) {
                chordPitches.add(melodyPitch)
            }

            val note = Note(
                pitches = chordPitches.sortedBy { it.midi }, // 音高順にソート
                startTime = index.toFloat(),
                duration = 1f,
                chordName = currentChord.displayName // コード名をセット
            )
            notes.add(note)
        }

        return Score(
            notes = notes,
            bpm = 120,
            totalBeats = numNotes.toFloat()
        )
    }

    /**
     * 重み付き確率分布に基づいて音高を選択
     * @param weights 各音高に対する重み
     * @return 選択された音高
     */
    private fun selectPitchByWeight(weights: Map<Pitch, Double>): Pitch {
        val totalWeight = weights.values.sum()
        val randomValue = random.nextDouble() * totalWeight

        var cumulativeWeight = 0.0
        for ((pitch, weight) in weights) {
            cumulativeWeight += weight
            if (randomValue <= cumulativeWeight) {
                return pitch
            }
        }

        // フォールバック（通常ここには到達しない）
        return weights.keys.first()
    }
}
