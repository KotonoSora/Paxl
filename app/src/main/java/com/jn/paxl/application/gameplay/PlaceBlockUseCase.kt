package com.jn.paxl.application.gameplay

import com.jn.paxl.domain.gameplay.GameRules
import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState

data class PlaceBlockResult(
    val newState: GameUiState,
    val previousGridSnapshot: Map<Coordinate, androidx.compose.ui.graphics.Color?>
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

        val (newCells, linesCleared) = GameRules.clearLines(currentCells, grid.size)

        val newAvailableBlocks = currentState.availableBlocks.filter { it.id != block.id }.let {
            if (it.isEmpty()) blockCatalog.randomBlocks(3) else it
        }

        val newScore = currentState.score + (block.shape.size * 10) + (linesCleared * 100)

        val updatedState = currentState.copy(
            grid = currentState.grid.copy(cells = newCells),
            availableBlocks = newAvailableBlocks,
            score = newScore,
            isGameOver = GameRules.checkGameOver(
                newCells,
                newAvailableBlocks,
                currentState.grid.size
            )
        )

        return PlaceBlockResult(updatedState, previousCells)
    }
}

