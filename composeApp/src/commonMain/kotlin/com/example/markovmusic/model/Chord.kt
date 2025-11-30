package com.example.markovmusic.model

/**
 * コード（和音）を表すenum
 * カノン進行: D -> A -> Bm -> F#m -> G -> D -> G -> A
 */
enum class Chord(
    val displayName: String,
    val notes: List<Pitch>,
    val weights: Map<Pitch, Double>
) {
    D("D",
        listOf(Pitch.D4, Pitch.F4, Pitch.A4),
        mapOf(
            Pitch.D4 to 0.3, Pitch.D5 to 0.3,
            Pitch.F4 to 0.15, Pitch.F5 to 0.15,
            Pitch.A4 to 0.3, Pitch.A5 to 0.3,
            Pitch.C4 to 0.05, Pitch.E4 to 0.05,
            Pitch.G4 to 0.05, Pitch.B4 to 0.05
        )
    ),

    A("A",
        listOf(Pitch.A4, Pitch.C5, Pitch.E5),
        mapOf(
            Pitch.A4 to 0.3, Pitch.A5 to 0.3,
            Pitch.C4 to 0.15, Pitch.C5 to 0.15,
            Pitch.E4 to 0.3, Pitch.E5 to 0.3,
            Pitch.D4 to 0.05, Pitch.F4 to 0.05,
            Pitch.G4 to 0.05, Pitch.B4 to 0.05
        )
    ),

    Bm("Bm",
        listOf(Pitch.B4, Pitch.D5, Pitch.F5),
        mapOf(
            Pitch.B4 to 0.3, Pitch.B5 to 0.3,
            Pitch.D4 to 0.15, Pitch.D5 to 0.15,
            Pitch.F4 to 0.15, Pitch.F5 to 0.15,
            Pitch.A4 to 0.05, Pitch.C4 to 0.05,
            Pitch.E4 to 0.05, Pitch.G4 to 0.05
        )
    ),

    Fm("F#m",
        listOf(Pitch.F4, Pitch.A4, Pitch.C5),
        mapOf(
            Pitch.F4 to 0.3, Pitch.F5 to 0.3,
            Pitch.A4 to 0.3, Pitch.A5 to 0.3,
            Pitch.C4 to 0.15, Pitch.C5 to 0.15,
            Pitch.D4 to 0.05, Pitch.E4 to 0.05,
            Pitch.G4 to 0.05, Pitch.B4 to 0.05
        )
    ),

    G("G",
        listOf(Pitch.G4, Pitch.B4, Pitch.D5),
        mapOf(
            Pitch.G4 to 0.3, Pitch.G5 to 0.3,
            Pitch.B4 to 0.3, Pitch.B5 to 0.3,
            Pitch.D4 to 0.15, Pitch.D5 to 0.15,
            Pitch.C4 to 0.05, Pitch.E4 to 0.05,
            Pitch.F4 to 0.05, Pitch.A4 to 0.05
        )
    );

    companion object {
        /**
         * カノン進行のコード順序
         */
        val CANON_PROGRESSION = listOf(D, A, Bm, Fm, G, D, G, A)
    }
}
