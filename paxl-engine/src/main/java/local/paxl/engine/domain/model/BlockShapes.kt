package local.paxl.engine.domain.model

/**
 * Block shape + color catalog. Colors are ARGB longs (no Compose dependency);
 * game-ui renders them via `Color(argb.toULong())`.
 */
object BlockShapes {
    private val blockColors = listOf(
        0xFFE91E63L, // Pink
        0xFF9C27B0L, // Purple
        0xFF2196F3L, // Blue
        0xFF4CAF50L, // Green
        0xFFFFC107L, // Amber
        0xFFFF5722L, // Deep Orange
        0xFF795548L // Brown
    )

    private val shapes = listOf(
        // 1x1
        listOf(Coordinate(0, 0)),
        // 1x2
        listOf(Coordinate(0, 0), Coordinate(1, 0)),
        // 1x3
        listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(2, 0)),
        // 2x2 Square
        listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(0, 1), Coordinate(1, 1)),
        // L-Shape
        listOf(Coordinate(0, 0), Coordinate(0, 1), Coordinate(1, 1)),
        // T-Shape
        listOf(Coordinate(0, 0), Coordinate(1, 0), Coordinate(2, 0), Coordinate(1, 1)),
        // 3x3 L-Shape
        listOf(
            Coordinate(0, 0), Coordinate(0, 1), Coordinate(0, 2), Coordinate(1, 2), Coordinate(2, 2)
        ),
        // 2x3 Rectangle
        listOf(
            Coordinate(0, 0),
            Coordinate(1, 0),
            Coordinate(0, 1),
            Coordinate(1, 1),
            Coordinate(0, 2),
            Coordinate(1, 2)
        )
    )

    fun randomBlocks(count: Int): List<Block> {
        return (1..count).map {
            Block(
                shape = shapes.random(), colorArgb = blockColors.random()
            )
        }
    }

    @Suppress("unused")
    fun getPreviewBlocks(): List<Block> {
        return shapes.mapIndexed { index, shape ->
            Block(
                shape = shape,
                colorArgb = blockColors[index % blockColors.size],
                id = "preview-block-$index"
            )
        }
    }
}
