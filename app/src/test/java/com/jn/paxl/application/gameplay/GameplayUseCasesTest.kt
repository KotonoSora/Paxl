package com.jn.paxl.application.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.domain.gameplay.GameRules
import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameplayUseCasesTest {

    private val singleCellBlock = Block(
        shape = listOf(Coordinate(0, 0)),
        color = Color.Blue,
        id = "single"
    )

    private val fakeCatalog = object : BlockCatalog {
        override fun randomBlocks(count: Int): List<Block> = List(count) { singleCellBlock.copy(id = "b$it") }
    }

    @Test
    fun `game rules reject placement on occupied cell`() {
        val occupied = mapOf(Coordinate(0, 0) to Color.Red)
        val grid = GridState(cells = occupied, size = 10)

        val canPlace = GameRules.canPlaceBlock(singleCellBlock, Coordinate(0, 0), grid)

        assertFalse(canPlace)
    }

    @Test
    fun `place block use case updates score and grid`() {
        val useCase = PlaceBlockUseCase(fakeCatalog)
        val initialState = GameUiState(
            grid = GridState(size = 10),
            availableBlocks = listOf(singleCellBlock),
            score = 0
        )

        val result = useCase(initialState, singleCellBlock, Coordinate(0, 0))

        assertNotNull(result)
        val newState = result!!.newState
        assertEquals(10, newState.score)
        assertNotNull(newState.grid.cells[Coordinate(0, 0)])
        assertEquals(3, newState.availableBlocks.size)
    }

    @Test
    fun `undo use case restores previous grid and deducts coins`() {
        val undoUseCase = UndoMoveUseCase(undoCost = 10)
        val history = mutableListOf<Map<Coordinate, Color?>>(emptyMap())
        val state = GameUiState(
            grid = GridState(cells = mapOf(Coordinate(0, 0) to Color.Blue), size = 10),
            coins = 100
        )

        val updated = undoUseCase(state, history)

        assertEquals(90, updated.coins)
        assertNull(updated.grid.cells[Coordinate(0, 0)])
        assertTrue(history.isEmpty())
    }
}

