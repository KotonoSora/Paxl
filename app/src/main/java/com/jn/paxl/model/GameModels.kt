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

data class LeaderboardEntry(
    val score: Int,
    val durationSeconds: Long,
    val recordedAtEpochMs: Long
)

enum class PlayMode {
    CLASSIC,
    LEVELS,
    DAILY
}

data class GameUiState(
    val grid: GridState = GridState(),
    val availableBlocks: List<Block> = emptyList(),
    val currentLevel: Int = 1,
    val playMode: PlayMode = PlayMode.CLASSIC,
    val targetScore: Int = 5000,
    val winTokenReward: Int = 25,
    val continueTokenCost: Int = 30,
    val sessionStartMs: Long = System.currentTimeMillis(),
    val score: Int = 0,
    val tokens: Int = 0,
    val isGameOver: Boolean = false,
    val isWin: Boolean = false,
    val isWinConditionSkipped: Boolean = false,
    val isClearing: Boolean = false,
    val clearAnimationId: Long = 0L,
    val clearingCells: Map<Coordinate, Color?> = emptyMap(),
    val highScore: Int = 0,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true
)
