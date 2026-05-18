package com.jn.paxl

import androidx.compose.ui.graphics.Color
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GridState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaxlGameModelsTest {

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
}
