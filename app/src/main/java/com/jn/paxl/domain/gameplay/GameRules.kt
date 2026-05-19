package com.jn.paxl.domain.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GridState

object GameRules {
    data class LineClearInfo(
        val cellsToClear: Set<Coordinate>,
        val linesCleared: Int
    )

    fun canPlaceBlock(block: Block, position: Coordinate, grid: GridState): Boolean {
        return block.shape.all { offset ->
            val x = position.x + offset.x
            val y = position.y + offset.y
            x in 0 until grid.size && y in 0 until grid.size && grid.cells[Coordinate(x, y)] == null
        }
    }

    fun findLineClearInfo(cells: Map<Coordinate, Color?>, size: Int): LineClearInfo? {
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
        cells: Map<Coordinate, Color?>,
        cellsToClear: Set<Coordinate>
    ): Map<Coordinate, Color?> {
        if (cellsToClear.isEmpty()) return cells

        val newCells = cells.toMutableMap()
        cellsToClear.forEach { coord -> newCells.remove(coord) }
        return newCells
    }

    fun clearLines(cells: Map<Coordinate, Color?>, size: Int): Pair<Map<Coordinate, Color?>, Int> {
        val clearInfo = findLineClearInfo(cells, size) ?: return Pair(cells, 0)
        return Pair(clearCells(cells, clearInfo.cellsToClear), clearInfo.linesCleared)
    }

    fun checkGameOver(
        cells: Map<Coordinate, Color?>,
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

