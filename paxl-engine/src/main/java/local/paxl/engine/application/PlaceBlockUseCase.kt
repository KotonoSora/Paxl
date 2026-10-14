package local.paxl.engine.application

import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.repository.BlockCatalog
import local.paxl.engine.domain.service.GameRules
import local.paxl.engine.domain.service.RewardPolicy

class PlaceBlockUseCase(private val blockCatalog: BlockCatalog) {
    operator fun invoke(
        currentState: GameState,
        block: Block,
        gridPosition: Coordinate
    ): PlaceBlockResult? {
        val grid = currentState.grid
        if (!GameRules.canPlaceBlock(block, gridPosition, grid)) return null

        val previousCells = grid.cells.toMap()
        val currentCells = grid.cells.toMutableMap()

        block.shape.forEach { offset ->
            val target = Coordinate(gridPosition.x + offset.x, gridPosition.y + offset.y)
            currentCells[target] = block.colorArgb
        }

        val lineClearInfo = GameRules.findLineClearInfo(currentCells, grid.size)
        val linesCleared = lineClearInfo?.linesCleared ?: 0
        val placedCells = currentCells.toMap()
        val clearedCells =
            lineClearInfo?.let { GameRules.clearCells(placedCells, it.cellsToClear) } ?: placedCells

        val newAvailableBlocks = currentState.availableBlocks.filter { it.id != block.id }.let {
            if (it.isEmpty()) blockCatalog.randomBlocks(3) else it
        }

        val newScore = currentState.score +
            (block.shape.size * RewardPolicy.SCORE_PER_CELL) +
            (linesCleared * RewardPolicy.SCORE_PER_LINE)
        val isClearing = lineClearInfo != null
        val clearingCells =
            lineClearInfo?.cellsToClear.orEmpty().associateWith { coord -> placedCells[coord] }

        val updatedState = currentState.copy(
            grid = currentState.grid.copy(cells = clearedCells),
            availableBlocks = newAvailableBlocks,
            score = newScore,
            linesCleared = currentState.linesCleared + linesCleared,
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
