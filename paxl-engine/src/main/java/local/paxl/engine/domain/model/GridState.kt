package local.paxl.engine.domain.model

/**
 * Sparse Block Blast-style board. A missing (or null-valued) entry means an
 * empty cell.
 *
 * New games default to a 12x12 board ([DEFAULT_SIZE]).
 * [size] is kept configurable so previews/tests can still use other sizes.
 */
data class GridState(
    val cells: Map<Coordinate, Long?> = emptyMap(),
    val size: Int = DEFAULT_SIZE
) {
    companion object {
        const val DEFAULT_SIZE = 12
    }

    fun isInside(coord: Coordinate): Boolean =
        coord.x in 0 until size && coord.y in 0 until size

    fun isEmptyAt(coord: Coordinate): Boolean =
        isInside(coord) && cells[coord] == null

    val filledCount: Int get() = cells.count { it.value != null }

    val isEmpty: Boolean get() = filledCount == 0

    fun containsAll(coords: Iterable<Coordinate>): Boolean =
        coords.all { cells[it] != null }
}
