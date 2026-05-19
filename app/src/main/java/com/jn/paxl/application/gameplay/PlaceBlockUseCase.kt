package com.jn.paxl.application.gameplay

import com.jn.paxl.domain.gameplay.GameRules
import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState

data class PlaceBlockResult(
    val newState: GameUiState,
    val previousGridSnapshot: Map<Coordinate, androidx.compose.ui.graphics.Color?>,
    val clearInfo: GameRules.LineClearInfo? = null
)

class PlaceBlockUseCase(private val blockCatalog: BlockCatalog) {
    operator fun invoke(
        currentState: GameUiState,
        block: Block,
        gridPosition: Coordinate
    ): PlaceBlockResult? {
        val grid = currentState.grid
        if (!GameRules.canPlaceBlock(block, gridPosition, grid)) return null

        val previousCells = grid.cells.toMap()
        val currentCells = grid.cells.toMutableMap()

        block.shape.forEach { offset ->
            val target = Coordinate(gridPosition.x + offset.x, gridPosition.y + offset.y)
            currentCells[target] = block.color
        }

        val lineClearInfo = GameRules.findLineClearInfo(currentCells, grid.size)
        val linesCleared = lineClearInfo?.linesCleared ?: 0
        val placedCells = currentCells.toMap()
        val clearedCells = lineClearInfo?.let { GameRules.clearCells(placedCells, it.cellsToClear) } ?: placedCells

        val newAvailableBlocks = currentState.availableBlocks.filter { it.id != block.id }.let {
            if (it.isEmpty()) blockCatalog.randomBlocks(3) else it
        }

        val newScore = currentState.score + (block.shape.size * 10) + (linesCleared * 100)
        val isClearing = lineClearInfo != null
        val clearingCells = lineClearInfo?.cellsToClear.orEmpty().associateWith { coord -> placedCells[coord] }

        val updatedState = currentState.copy(
            grid = currentState.grid.copy(cells = clearedCells),
            availableBlocks = newAvailableBlocks,
            score = newScore,
            isGameOver = GameRules.checkGameOver(
                clearedCells,
                newAvailableBlocks,
                currentState.grid.size
            ),
            isClearing = isClearing,
            clearAnimationId = if (isClearing) currentState.clearAnimationId + 1 else currentState.clearAnimationId,
            clearingCells = clearingCells
        )

        return PlaceBlockResult(updatedState, previousCells, lineClearInfo)
    }
}

