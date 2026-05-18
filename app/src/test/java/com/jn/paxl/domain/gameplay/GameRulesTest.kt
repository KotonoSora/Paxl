package com.jn.paxl.domain.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GridState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRulesTest {

    private val singleCell = Block(
        shape = listOf(Coordinate(0, 0)),
        color = Color.Cyan,
        id = "single"
    )

    @Test
    fun `cannot place block out of bounds`() {
        val grid = GridState(size = 10)

        val canPlace = GameRules.canPlaceBlock(singleCell, Coordinate(10, 0), grid)

        assertFalse(canPlace)
    }

    @Test
    fun `clearLines clears both a full row and a full column`() {
        val size = 3
        val occupied = buildMap {
            for (x in 0 until size) put(Coordinate(x, 0), Color.Red)
            for (y in 0 until size) put(Coordinate(1, y), Color.Blue)
        }

        val (newCells, clearedLines) = GameRules.clearLines(occupied, size)

        assertEquals(2, clearedLines)
        assertTrue(newCells.isEmpty())
    }

    @Test
    fun `checkGameOver is true when no position can fit any block`() {
        val grid = mapOf(
            Coordinate(0, 0) to Color.Red,
            Coordinate(1, 0) to Color.Red,
            Coordinate(0, 1) to Color.Red
        )
        val oneByTwo = Block(
            shape = listOf(Coordinate(0, 0), Coordinate(1, 0)),
            color = Color.Green,
            id = "h2"
        )

        val isGameOver = GameRules.checkGameOver(cells = grid, availableBlocks = listOf(oneByTwo), size = 2)

        assertTrue(isGameOver)
    }
}

