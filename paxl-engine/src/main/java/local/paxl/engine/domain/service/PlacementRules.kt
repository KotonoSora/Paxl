package local.paxl.engine.domain.service

import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GridState

/** Board-occupancy queries: fit checks, placement search, game-over. */
object PlacementRules {
    fun canPlaceBlock(block: Block, position: Coordinate, grid: GridState): Boolean {
        return block.shape.all { offset ->
            val x = position.x + offset.x
            val y = position.y + offset.y
            x in 0 until grid.size && y in 0 until grid.size && grid.cells[Coordinate(x, y)] == null
        }
    }

    /** Every valid board origin for [block] (used by game-over + hints). */
    fun findPlacements(block: Block, grid: GridState): List<Coordinate> {
        // Bound the scan by the shape extents so wide pieces don't scan OOB.
        val minOriginX = -block.minX
        val maxOriginX = grid.size - 1 - block.maxX
        val minOriginY = -block.minY
        val maxOriginY = grid.size - 1 - block.maxY
        if (maxOriginX < minOriginX || maxOriginY < minOriginY) return emptyList()
        val out = ArrayList<Coordinate>()
        for (y in minOriginY..maxOriginY) {
            for (x in minOriginX..maxOriginX) {
                val origin = Coordinate(x, y)
                if (canPlaceBlock(block, origin, grid)) out.add(origin)
            }
        }
        return out
    }

    fun canPlaceAnywhere(block: Block, grid: GridState): Boolean {
        val minOriginX = -block.minX
        val maxOriginX = grid.size - 1 - block.maxX
        val minOriginY = -block.minY
        val maxOriginY = grid.size - 1 - block.maxY
        for (y in minOriginY..maxOriginY) {
            for (x in minOriginX..maxOriginX) {
                if (canPlaceBlock(block, Coordinate(x, y), grid)) return true
            }
        }
        return false
    }

    /** True when at least one tray piece fits somewhere (run still alive). */
    fun hasAnyMove(blocks: List<Block>, grid: GridState): Boolean =
        blocks.any { canPlaceAnywhere(it, grid) }

    fun checkGameOver(
        cells: Map<Coordinate, Long?>,
        availableBlocks: List<Block>,
        size: Int
    ): Boolean {
        if (availableBlocks.isEmpty()) return false
        val gridState = GridState(cells, size)
        return !hasAnyMove(availableBlocks, gridState)
    }
}
