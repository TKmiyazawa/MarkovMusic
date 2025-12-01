package com.example.markovmusic.generator

import com.example.markovmusic.model.Chord
import com.example.markovmusic.model.GeminiNoteResponse
import com.example.markovmusic.model.Note
import com.example.markovmusic.model.Pitch
import com.example.markovmusic.model.Score
import com.example.markovmusic.network.GeminiApiClient

/**
 * Gemini AIを使用してメロディを生成するジェネレータ
 * Ktor HTTPクライアントを使用した共通実装（AndroidとiOS両方で動作）
 */
class GeminiMelodyGenerator(private val apiKey: String) {

    private val apiClient = GeminiApiClient(apiKey)

    /**
     * Gemini AIを使用してメロディを生成
     */
    suspend fun generateMelody(): Result<Score> {
        return try {
            if (apiKey.isEmpty()) {
                return Result.failure(Exception("APIキーが設定されていません"))
            }

            val prompt = buildPrompt()
            val result = apiClient.generateMelody(prompt)

            result.fold(
                onSuccess = { geminiNotes ->
                    val notes = parseNotes(geminiNotes)
                    if (notes.isEmpty()) {
                        return Result.failure(Exception("メロディのパースに失敗しました"))
                    }
                    val score = Score(
                        notes = notes,
                        bpm = 120,
                        totalBeats = notes.size.toFloat()
                    )
                    Result.success(score)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(Exception("生成エラー: ${e.message}"))
        }
    }

    private fun buildPrompt(): String {
        val availablePitches = Pitch.entries.joinToString(", ") { it.name }

        return """
あなたは天才的な作曲家です。

パッヘルベルのカノン（D Major）のコード進行 [D, A, Bm, F#m, G, D, G, A] に完璧にマッチする、
美しく感動的なメロディを30秒分（60音符、BPM 120）生成してください。

## 重要な制約事項:
1. 以下のピッチのみ使用可能です: $availablePitches
2. 各音符は1拍 (duration: 1.0) 固定です
3. コード進行は4拍ごとに変わります（計8コード × 4拍 = 32拍を2周）
4. メロディはコードトーンを中心に、経過音を適度に含めてください
5. 音域はC4〜B5の範囲内に収めてください

## コード進行と推奨メロディ音:
- D (拍 1-4, 33-36): D4, F4, A4, D5, F5, A5
- A (拍 5-8, 37-40): A4, C5, E5, A5
- Bm (拍 9-12, 41-44): B4, D5, F5
- F#m (拍 13-16, 45-48): F4, A4, C5
- G (拍 17-20, 49-52): G4, B4, D5, G5
- D (拍 21-24, 53-56): D4, F4, A4, D5
- G (拍 25-28, 57-60): G4, B4, D5, G5
- A (拍 29-32, 61-64): A4, C5, E5

## 出力形式:
JSON配列として60個の音符オブジェクトを出力してください。

[
  {"pitch": "D5", "duration": 1.0},
  {"pitch": "C5", "duration": 1.0},
  {"pitch": "B4", "duration": 1.0},
  ...
]

注意: pitchの値は必ず上記の利用可能なピッチ名と完全に一致させてください（例: D5, C4, F4など）。
シャープやフラットは使用せず、列挙された名前のみを使ってください。
        """.trimIndent()
    }

    private fun parseNotes(geminiNotes: List<GeminiNoteResponse>): List<Note> {
        val notes = mutableListOf<Note>()
        val progression = Chord.CANON_PROGRESSION

        geminiNotes.forEachIndexed { index, geminiNote ->
            val pitch = geminiNote.toPitch()
            if (pitch != null) {
                val chordIndex = (index / 4) % progression.size
                val currentChord = progression[chordIndex]

                val chordPitches = mutableListOf<Pitch>()
                chordPitches.addAll(currentChord.notes)

                if (!chordPitches.contains(pitch)) {
                    chordPitches.add(pitch)
                }

                notes.add(
                    Note(
                        pitches = chordPitches.sortedBy { it.midi },
                        startTime = index.toFloat(),
                        duration = 1f,
                        chordName = currentChord.displayName
                    )
                )
            }
        }

        // 60音符に満たない場合はパディング
        while (notes.size < 60) {
            val lastNote = notes.lastOrNull()
            if (lastNote != null) {
                notes.add(
                    lastNote.copy(startTime = notes.size.toFloat())
                )
            } else {
                val chordIndex = (notes.size / 4) % progression.size
                val currentChord = progression[chordIndex]
                notes.add(
                    Note(
                        pitches = currentChord.notes,
                        startTime = notes.size.toFloat(),
                        duration = 1f,
                        chordName = currentChord.displayName
                    )
                )
            }
        }

        return notes.take(60)
    }
}
