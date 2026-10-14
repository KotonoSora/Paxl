package local.paxl.engine.application

import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Combo
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.DragPreview
import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.repository.BlockCatalog
import local.paxl.engine.domain.service.LineClearing
import local.paxl.engine.domain.service.PlacementRules
import local.paxl.engine.domain.service.RewardPolicy

/**
 * Block Blast placement: stamp the piece, blast full rows+cols together,
 * then score `placed + (cleared*10 + lineBonus) * combo + allClear`.
 * The combo transition itself lives in the [Combo] value object.
 */
class PlaceBlockUseCase(private val blockCatalog: BlockCatalog) {
    /**
     * Finger-release drop: stamps the piece exactly where the displayed ghost
     * is. Takes the [DragPreview] produced by `DropSnapping.previewDrop` for
     * the current frame and places [preview.block] at [preview.origin]
     * verbatim — never re-snaps, never re-maps the pointer — so the drop
     * cannot resolve to a different cell than the ghost. Null origin
     * (no fit near the finger) returns null so the caller cancels the drag.
     */
    fun placeAtPreview(
        currentState: GameState,
        preview: DragPreview
    ): PlaceBlockResult? {
        // Self-check 1: no snapped origin (red ghost / off board) → cancel.
        val origin = preview.origin ?: return null
        // Self-check 2: the ghost's piece must still be in the tray (a
        // mid-drag reshuffle/undo deals new ids). Cancel softly instead of
        // hitting the strict require() below.
        if (currentState.availableBlocks.none { it.id == preview.block.id }) return null
        // Self-check 3 (inside invoke): strict fit check on the live grid.
        return invoke(currentState, preview.block, origin)
    }

    operator fun invoke(
        currentState: GameState,
        block: Block,
        gridPosition: Coordinate
    ): PlaceBlockResult? {
        val grid = currentState.grid
        if (!PlacementRules.canPlaceBlock(block, gridPosition, grid)) return null
        require(currentState.availableBlocks.any { it.id == block.id }) {
            "Block ${block.id} is not in the tray"
        }

        val currentCells = grid.cells.toMutableMap()
        block.shape.forEach { offset ->
            val target = Coordinate(gridPosition.x + offset.x, gridPosition.y + offset.y)
            currentCells[target] = block.colorArgb
        }

        val lineClearInfo = LineClearing.findLineClearInfo(currentCells, grid.size)
        val linesCleared = lineClearInfo?.linesCleared ?: 0
        val placedCells = currentCells.toMap()
        val clearedCells =
            lineClearInfo?.let { LineClearing.clearCells(placedCells, it.cellsToClear) }
                ?: placedCells
        val isAllClear = lineClearInfo != null && clearedCells.none { it.value != null }

        val combo = currentState.combo.onPlacement(linesCleared > 0)
        val gained = RewardPolicy.moveScore(
            placedCells = block.shape.size,
            clearedCells = lineClearInfo?.cellsToClear?.size ?: 0,
            lines = linesCleared,
            combo = combo.value,
            isAllClear = isAllClear
        )

        val newAvailableBlocks = currentState.availableBlocks.filter { it.id != block.id }.let {
            if (it.isEmpty()) blockCatalog.deal(3) else it
        }

        val isClearing = lineClearInfo != null
        val updatedState = currentState.copy(
            grid = currentState.grid.copy(cells = clearedCells),
            availableBlocks = newAvailableBlocks,
            score = currentState.score + gained,
            linesCleared = currentState.linesCleared + linesCleared,
            isGameOver = PlacementRules.checkGameOver(
                clearedCells,
                newAvailableBlocks,
                currentState.grid.size
            ),
            isClearing = isClearing,
            clearAnimationId = if (isClearing) currentState.clearAnimationId + 1 else currentState.clearAnimationId,
            clearingCells = lineClearInfo?.cellsToClear.orEmpty()
                .associateWith { coord -> placedCells[coord] },
            combo = combo
        )

        return PlaceBlockResult(updatedState, lineClearInfo)
    }
}
