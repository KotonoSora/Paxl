package com.kotonosora.paxl.model

import androidx.compose.ui.graphics.Color

object ShapeLibrary {
    private val blockColors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFFC107), // Amber
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF795548)  // Brown
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
            Coordinate(0, 0),
            Coordinate(0, 1),
            Coordinate(0, 2),
            Coordinate(1, 2),
            Coordinate(2, 2)
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

    fun getRandomBlocks(count: Int): List<Block> {
        return (1..count).map {
            Block(
                shape = shapes.random(),
                color = blockColors.random()
            )
        }
    }
}
