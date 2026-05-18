package com.jn.paxl.application.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ReshuffleBlocksUseCaseTest {

    private val fakeCatalog = object : BlockCatalog {
        override fun randomBlocks(count: Int): List<Block> =
            List(count) { index ->
                Block(
                    shape = listOf(Coordinate(0, 0)),
                    color = Color.Yellow,
                    id = "reshuffled-$index"
                )
            }
    }

    @Test
    fun `reshuffle returns same state when coins are insufficient`() {
        val useCase = ReshuffleBlocksUseCase(fakeCatalog, reshuffleCost = 25)
        val state = GameUiState(coins = 20)

        val result = useCase(state)

        assertSame(state, result)
    }

    @Test
    fun `reshuffle replaces blocks and deducts coins when enough balance`() {
        val useCase = ReshuffleBlocksUseCase(fakeCatalog, reshuffleCost = 25)
        val before = GameUiState(
            availableBlocks = listOf(
                Block(shape = listOf(Coordinate(0, 0)), color = Color.Blue, id = "before")
            ),
            coins = 100
        )

        val result = useCase(before)

        assertEquals(75, result.coins)
        assertEquals(3, result.availableBlocks.size)
        assertNotEquals(before.availableBlocks.map { it.id }, result.availableBlocks.map { it.id })
    }
}

