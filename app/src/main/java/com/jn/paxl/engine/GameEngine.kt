package com.jn.paxl.engine

import androidx.compose.ui.graphics.Color
import com.jn.paxl.domain.gameplay.GameRules
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GridState

object GameEngine {
    fun canPlaceBlock(block: Block, position: Coordinate, grid: GridState): Boolean {
        return GameRules.canPlaceBlock(block, position, grid)
    }

    fun clearLines(cells: Map<Coordinate, Color?>, size: Int): Pair<Map<Coordinate, Color?>, Int> {
        return GameRules.clearLines(cells, size)
    }

    fun checkGameOver(
        cells: Map<Coordinate, Color?>,
        availableBlocks: List<Block>,
        size: Int
    ): Boolean {
        return GameRules.checkGameOver(cells, availableBlocks, size)
    }
}
