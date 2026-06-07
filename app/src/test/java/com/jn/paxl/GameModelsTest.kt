package com.jn.paxl

import androidx.compose.ui.graphics.Color
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState
import com.jn.paxl.model.PlayMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameModelsTest {

    @Test
    fun `block gets unique id by default`() {
        val first = Block(shape = listOf(Coordinate(0, 0)), color = Color.Blue)
        val second = Block(shape = listOf(Coordinate(0, 0)), color = Color.Blue)

        assertNotEquals(first.id, second.id)
    }

    @Test
    fun `grid state defaults to empty 10x10 board`() {
        val grid = GridState()

        assertEquals(10, grid.size)
        assertTrue(grid.cells.isEmpty())
    }

    @Test
    fun `coordinates with same values are equal`() {
        val c1 = Coordinate(5, 3)
        val c2 = Coordinate(5, 3)
        val c3 = Coordinate(3, 5)

        assertEquals(c1, c2)
        assertEquals(c1.hashCode(), c2.hashCode())
        assertNotEquals(c1, c3)
    }

    @Test
    fun `block with same properties but different ids are not equal`() {
        val shape = listOf(Coordinate(0, 0))
        val color = Color.Red
        val b1 = Block(shape, color, id = "1")
        val b2 = Block(shape, color, id = "2")

        assertNotEquals(b1, b2)
    }

    @Test
    fun `game ui state has sensible defaults`() {
        val state = GameUiState()

        assertEquals(0, state.score)
        assertEquals(0, state.tokens)
        assertEquals(PlayMode.CLASSIC, state.playMode)
        assertTrue(state.availableBlocks.isEmpty())
        assertTrue(state.grid.cells.isEmpty())
        assertEquals(10, state.grid.size)
    }

    @Test
    fun `game ui state with same properties are equal`() {
        val state1 = GameUiState(score = 100)
        val state2 = GameUiState(score = 100)

        assertEquals(state1, state2)
    }
}
