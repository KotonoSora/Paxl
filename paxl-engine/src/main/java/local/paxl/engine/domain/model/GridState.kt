package local.paxl.engine.domain.model

/**
 * Sparse 10x10 board. A missing (or null-valued) entry means an empty cell.
 */
data class GridState(
    val cells: Map<Coordinate, Long?> = emptyMap(),
    val size: Int = 10
)
