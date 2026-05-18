package com.jn.paxl.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Coordinate(val x: Int, val y: Int)

data class Block(
    val shape: List<Coordinate>,
    val color: Color,
    val id: String = UUID.randomUUID().toString()
)

data class GridState(
    val cells: Map<Coordinate, Color?> = emptyMap(),
    val size: Int = 10
)

data class GameUiState(
    val grid: GridState = GridState(),
    val availableBlocks: List<Block> = emptyList(),
    val currentLevel: Int = 1,
    val score: Int = 0,
    val tokens: Int = 0,
    val isGameOver: Boolean = false,
    val highScore: Int = 0,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true
)
