package com.example.markovmusic.model

/**
 * 音高を表すenum
 * @param midi MIDI番号（C4 = 60）
 * @param frequency 周波数（Hz）
 * @param displayName 表示名
 */
enum class Pitch(val midi: Int, val frequency: Double, val displayName: String) {
    // オクターブ4（中音域）
    C4(60, 261.63, "C"),
    D4(62, 293.66, "D"),
    E4(64, 329.63, "E"),
    F4(65, 349.23, "F"),
    G4(67, 392.00, "G"),
    A4(69, 440.00, "A"),
    B4(71, 493.88, "B"),

    // オクターブ5（高音域）
    C5(72, 523.25, "C"),
    D5(74, 587.33, "D"),
    E5(76, 659.25, "E"),
    F5(77, 698.46, "F"),
    G5(79, 783.99, "G"),
    A5(81, 880.00, "A"),
    B5(83, 987.77, "B");

    companion object {
        fun fromMidi(midi: Int): Pitch? = values().find { it.midi == midi }
    }
}
