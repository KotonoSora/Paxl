package com.jn.paxl.domain.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GridState

object GameRules {
    fun canPlaceBlock(block: Block, position: Coordinate, grid: GridState): Boolean {
        return block.shape.all { offset ->
            val x = position.x + offset.x
            val y = position.y + offset.y
            x in 0 until grid.size && y in 0 until grid.size && grid.cells[Coordinate(x, y)] == null
        }
    }

    fun clearLines(cells: Map<Coordinate, Color?>, size: Int): Pair<Map<Coordinate, Color?>, Int> {
        val rowsToClear = (0 until size).filter { y ->
            (0 until size).all { x -> cells[Coordinate(x, y)] != null }
        }
        val colsToClear = (0 until size).filter { x ->
            (0 until size).all { y -> cells[Coordinate(x, y)] != null }
        }

        if (rowsToClear.isEmpty() && colsToClear.isEmpty()) return Pair(cells, 0)

        val newCells = cells.toMutableMap()
        rowsToClear.forEach { y ->
            (0 until size).forEach { x -> newCells.remove(Coordinate(x, y)) }
        }
        colsToClear.forEach { x ->
            (0 until size).forEach { y -> newCells.remove(Coordinate(x, y)) }
        }

        return Pair(newCells, rowsToClear.size + colsToClear.size)
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

