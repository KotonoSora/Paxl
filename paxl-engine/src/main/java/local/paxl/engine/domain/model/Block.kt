package local.paxl.engine.domain.model

/**
 * A placeable block (domain entity). Colors are ARGB longs so this module
 * stays pure Kotlin (no Compose); game-ui converts with `Color(argb)` at the
 * render boundary.
 *
 * Shapes use top-left-normalized [Coordinate] offsets (min x/y == 0) so the
 * [Coordinate] passed to the domain services is the board origin (top-left
 * of the bounding box).
 *
 * Identity is explicit: callers (catalogs, previews) supply [id] via the
 * [local.paxl.engine.domain.repository.BlockIdGenerator] port, keeping this
 * model deterministic and free of UUID/platform concerns.
 */
data class Block(
    val shape: List<Coordinate>,
    val colorArgb: Long,
    val id: String
) {
    val cellCount: Int get() = shape.size

    val minX: Int get() = shape.minOfOrNull { it.x } ?: 0
    val maxX: Int get() = shape.maxOfOrNull { it.x } ?: 0
    val minY: Int get() = shape.minOfOrNull { it.y } ?: 0
    val maxY: Int get() = shape.maxOfOrNull { it.y } ?: 0

    /** Bounding-box width/height in cells. */
    val width: Int get() = maxX - minX + 1
    val height: Int get() = maxY - minY + 1

    /** All board cells this block would occupy at [origin]. */
    fun cellsAt(origin: Coordinate): List<Coordinate> =
        shape.map { Coordinate(origin.x + it.x, origin.y + it.y) }
}
