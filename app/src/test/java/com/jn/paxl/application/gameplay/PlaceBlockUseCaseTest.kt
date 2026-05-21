package com.jn.paxl.application.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceBlockUseCaseTest {

    private class FakeCatalog : BlockCatalog {
        var requests = 0
        override fun randomBlocks(count: Int): List<Block> {
            requests += 1
            return List(count) { index ->
                Block(
                    shape = listOf(Coordinate(0, 0)),
                    color = Color.Yellow,
                    id = "generated-$index"
                )
            }
        }
    }

    @Test
    fun `returns null when block placement is invalid`() {
        val catalog = FakeCatalog()
        val useCase = PlaceBlockUseCase(catalog)
        val state = GameUiState(
            grid = GridState(cells = mapOf(Coordinate(0, 0) to Color.Red), size = 10),
            availableBlocks = listOf(
                Block(shape = listOf(Coordinate(0, 0)), color = Color.Blue, id = "active")
            )
        )

        val result = useCase(state, state.availableBlocks.first(), Coordinate(0, 0))

        assertNull(result)
        assertEquals(0, catalog.requests)
    }

    @Test
    fun `removes only played block and does not refill when blocks remain`() {
        val catalog = FakeCatalog()
        val useCase = PlaceBlockUseCase(catalog)
        val played = Block(shape = listOf(Coordinate(0, 0)), color = Color.Blue, id = "played")
        val keepA = Block(shape = listOf(Coordinate(0, 0)), color = Color.Red, id = "keep-a")
        val keepB = Block(shape = listOf(Coordinate(0, 0)), color = Color.Green, id = "keep-b")
        val state = GameUiState(
            grid = GridState(size = 10),
            availableBlocks = listOf(played, keepA, keepB),
            score = 20
        )

        val result = useCase(state, played, Coordinate(2, 2))

        assertNotNull(result)
        val newState = result!!.newState
        assertEquals(30, newState.score)
        assertEquals(listOf("keep-a", "keep-b"), newState.availableBlocks.map { it.id })
        assertEquals(0, catalog.requests)
    }

    @Test
    fun `refills to three blocks when the played block is the last available one`() {
        val catalog = FakeCatalog()
        val useCase = PlaceBlockUseCase(catalog)
        val onlyBlock = Block(shape = listOf(Coordinate(0, 0)), color = Color.Blue, id = "only")
        val state = GameUiState(
            grid = GridState(size = 10),
            availableBlocks = listOf(onlyBlock)
        )

        val result = useCase(state, onlyBlock, Coordinate(0, 0))

        assertNotNull(result)
        val newState = result!!.newState
        assertEquals(1, catalog.requests)
        assertEquals(3, newState.availableBlocks.size)
        assertTrue(newState.availableBlocks.all { it.id.startsWith("generated-") })
    }

    @Test
    fun `awards line clear bonus and clears occupied row`() {
        val catalog = FakeCatalog()
        val useCase = PlaceBlockUseCase(catalog)
        val oneCell = Block(shape = listOf(Coordinate(0, 0)), color = Color.Cyan, id = "single")

        val occupied = buildMap {
            for (x in 0 until 9) {
                put(Coordinate(x, 0), Color.Magenta)
            }
        }

        val state = GameUiState(
            grid = GridState(cells = occupied, size = 10),
            availableBlocks = listOf(
                oneCell,
                oneCell.copy(id = "other"),
                oneCell.copy(id = "other2")
            ),
            score = 0
        )

        val result = useCase(state, oneCell, Coordinate(9, 0))

        assertNotNull(result)
        val newState = result!!.newState
        assertEquals(110, newState.score)
        assertTrue(newState.isClearing)
        assertEquals(10, newState.clearingCells.size)
        assertTrue((0 until 10).all { x -> newState.grid.cells[Coordinate(x, 0)] == null })
    }
}

