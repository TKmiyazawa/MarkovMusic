package com.example.markovmusic.model

import kotlinx.serialization.Serializable

/**
 * Gemini APIからのレスポンスをパースするためのデータクラス
 *
 * 例: {"pitch": "D5", "duration": 0.5}
 */
@Serializable
data class GeminiNoteResponse(
    val pitch: String,
    val duration: Float
) {
    /**
     * GeminiNoteResponseをアプリ内のPitchに変換
     * サポートされている形式: "C4", "D#5", "Bb4" など
     */
    fun toPitch(): Pitch? {
        // ピッチ文字列をパース（例: "D5", "F#4", "Bb5"）
        val normalized = pitch.uppercase()
            .replace("♯", "#")
            .replace("♭", "B")

        // 基本音名とオクターブを抽出
        val noteMatch = Regex("([A-G])([#B]?)([0-9])").find(normalized) ?: return null
        val (noteName, accidental, octaveStr) = noteMatch.destructured
        val octave = octaveStr.toIntOrNull() ?: return null

        // MIDIノート番号を計算
        val baseNotes = mapOf(
            "C" to 0, "D" to 2, "E" to 4, "F" to 5,
            "G" to 7, "A" to 9, "B" to 11
        )
        val baseNote = baseNotes[noteName] ?: return null

        val accidentalOffset = when (accidental) {
            "#" -> 1
            "B" -> -1  // フラット (Bb = B-flat)
            else -> 0
        }

        val midiNote = (octave + 1) * 12 + baseNote + accidentalOffset

        // 対応するPitchを検索
        return Pitch.fromMidi(midiNote)
    }
}
