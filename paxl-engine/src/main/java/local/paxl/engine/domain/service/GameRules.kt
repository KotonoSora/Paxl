package local.paxl.engine.domain.service

import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GridState
import local.paxl.engine.domain.model.LineClearInfo

object GameRules {
    fun canPlaceBlock(block: Block, position: Coordinate, grid: GridState): Boolean {
        return block.shape.all { offset ->
            val x = position.x + offset.x
            val y = position.y + offset.y
            x in 0 until grid.size && y in 0 until grid.size && grid.cells[Coordinate(x, y)] == null
        }
    }

    fun findLineClearInfo(cells: Map<Coordinate, Long?>, size: Int): LineClearInfo? {
        val rowsToClear = (0 until size).filter { y ->
            (0 until size).all { x -> cells[Coordinate(x, y)] != null }
        }
        val colsToClear = (0 until size).filter { x ->
            (0 until size).all { y -> cells[Coordinate(x, y)] != null }
        }

        if (rowsToClear.isEmpty() && colsToClear.isEmpty()) return null

        val cellsToClear = mutableSetOf<Coordinate>()
        rowsToClear.forEach { y ->
            (0 until size).forEach { x -> cellsToClear.add(Coordinate(x, y)) }
        }
        colsToClear.forEach { x ->
            (0 until size).forEach { y -> cellsToClear.add(Coordinate(x, y)) }
        }

        return LineClearInfo(
            cellsToClear = cellsToClear,
            linesCleared = rowsToClear.size + colsToClear.size
        )
    }

    fun clearCells(
        cells: Map<Coordinate, Long?>,
        cellsToClear: Set<Coordinate>
    ): Map<Coordinate, Long?> {
        if (cellsToClear.isEmpty()) return cells

        val newCells = cells.toMutableMap()
        cellsToClear.forEach { coord -> newCells.remove(coord) }
        return newCells
    }

    fun clearLines(cells: Map<Coordinate, Long?>, size: Int): Pair<Map<Coordinate, Long?>, Int> {
        val clearInfo = findLineClearInfo(cells, size) ?: return Pair(cells, 0)
        return Pair(clearCells(cells, clearInfo.cellsToClear), clearInfo.linesCleared)
    }

    fun checkGameOver(
        cells: Map<Coordinate, Long?>,
        availableBlocks: List<Block>,
        size: Int
    ): Boolean {
        if (availableBlocks.isEmpty()) return false

        val gridState = GridState(cells, size)
        return availableBlocks.none { block ->
            (0 until size).any { x ->
                (0 until size).any { y ->
                    canPlaceBlock(block, Coordinate(x, y), gridState)
                }
            }
        }
    }
}
