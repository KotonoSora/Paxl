package local.paxl.engine.domain.model

/**
 * Pure shape data for the 7 tetrominoes in all rotation states (19 states).
 *
 * No colors, no weights, no randomness — just geometry in top-left-normalized
 * [Coordinate] offsets. Dealing policy lives in
 * [local.paxl.engine.domain.service.BlockDealPolicy]; randomness lives in the
 * infrastructure catalog.
 */
object TetrominoShapes {
    private fun c(x: Int, y: Int) = Coordinate(x, y)

    /** All rotation states for [type], in a fixed showcase order. */
    fun rotations(type: TetrominoType): List<List<Coordinate>> = when (type) {
        TetrominoType.I -> listOf(
            listOf(c(0, 0), c(1, 0), c(2, 0), c(3, 0)),
            listOf(c(0, 0), c(0, 1), c(0, 2), c(0, 3))
        )
        TetrominoType.O -> listOf(
            listOf(c(0, 0), c(1, 0), c(0, 1), c(1, 1))
        )
        TetrominoType.T -> listOf(
            listOf(c(0, 0), c(1, 0), c(2, 0), c(1, 1)), // stem down
            listOf(c(1, 0), c(0, 1), c(1, 1), c(2, 1)), // stem up
            listOf(c(0, 0), c(0, 1), c(0, 2), c(1, 1)), // stem right
            listOf(c(1, 0), c(1, 1), c(1, 2), c(0, 1)) // stem left
        )
        TetrominoType.S -> listOf(
            listOf(c(1, 0), c(2, 0), c(0, 1), c(1, 1)), // horizontal
            listOf(c(0, 0), c(0, 1), c(1, 1), c(1, 2)) // vertical
        )
        TetrominoType.Z -> listOf(
            listOf(c(0, 0), c(1, 0), c(1, 1), c(2, 1)), // horizontal
            listOf(c(1, 0), c(1, 1), c(0, 1), c(0, 2)) // vertical
        )
        TetrominoType.J -> listOf(
            listOf(c(0, 0), c(1, 0), c(2, 0), c(0, 1)), // flat, foot down-left
            listOf(c(0, 0), c(1, 0), c(1, 1), c(1, 2)), // bar right, nub top-left
            listOf(c(2, 0), c(0, 1), c(1, 1), c(2, 1)), // flat, foot up-right
            listOf(c(0, 0), c(0, 1), c(0, 2), c(1, 2)) // bar left, nub bottom-right
        )
        TetrominoType.L -> listOf(
            listOf(c(0, 0), c(1, 0), c(2, 0), c(2, 1)), // flat, foot down-right
            listOf(c(0, 0), c(1, 0), c(0, 1), c(0, 2)), // bar left, nub top-right
            listOf(c(0, 0), c(0, 1), c(1, 1), c(2, 1)), // flat, foot up-left
            listOf(c(1, 0), c(1, 1), c(0, 2), c(1, 2)) // bar right, nub bottom-left
        )
    }

    /** Every rotation state of every type (19 entries). */
    val allRotationStates: List<Pair<TetrominoType, List<Coordinate>>>
        get() = TetrominoType.entries.flatMap { type ->
            rotations(type).map { shape -> type to shape }
        }
}
