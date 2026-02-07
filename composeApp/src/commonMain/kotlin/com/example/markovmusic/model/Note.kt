package com.example.markovmusic.model

/**
 * 音符を表すデータクラス（和音対応）
 * @param pitches 音高のリスト（単音の場合は1つ、和音の場合は複数）
 * @param startTime 開始時間（拍）
 * @param duration 音の長さ（拍）
 * @param chordName コード名（マルコフ連鎖の場合のみ、例: "D", "A", "Bm"）
 */
data class Note(
    val pitches: List<Pitch>,
    val startTime: Float,
    val duration: Float = 1f,
    val chordName: String? = null
) {
    /**
     * 単音用コンストラクタ（後方互換性）
     */
    constructor(pitch: Pitch, startTime: Float, duration: Float = 1f) : this(
        pitches = listOf(pitch),
        startTime = startTime,
        duration = duration,
        chordName = null
    )

    /**
     * 和音かどうか
     */
    val isChord: Boolean
        get() = pitches.size > 1

    /**
     * 五線譜上の位置を計算（C4を基準線とする）
     * 和音の場合は各音の位置のリスト
     */
    val staffPositions: List<Int>
        get() = pitches.map { it.midi - Pitch.C4.midi }

    /**
     * 後方互換性のため、最初の音のstaffPositionを返す
     */
    val staffPosition: Int
        get() = staffPositions.firstOrNull() ?: 0

    /**
     * メロディ音（最も高い音）を取得
     * 五線譜に表示する音
     */
    val melodyPitch: Pitch
        get() = pitches.maxByOrNull { it.midi } ?: pitches.first()
}
