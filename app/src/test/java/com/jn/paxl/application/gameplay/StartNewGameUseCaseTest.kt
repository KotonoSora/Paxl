package com.jn.paxl.application.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StartNewGameUseCaseTest {

    private val fakeCatalog = object : BlockCatalog {
        override fun randomBlocks(count: Int): List<Block> {
            return List(count) { index ->
                Block(
                    shape = listOf(Coordinate(0, 0)),
                    color = Color.Magenta,
                    id = "start-$index"
                )
            }
        }
    }

    @Test
    fun `start new game resets board while preserving persistent values`() {
        val useCase = StartNewGameUseCase(fakeCatalog)
        val previous = GameUiState(
            score = 250,
            tokens = 420,
            highScore = 999,
            soundEnabled = false,
            musicEnabled = false,
            isGameOver = true
        )

        val result = useCase(previousState = previous, level = 3)

        assertEquals(0, result.score)
        assertEquals(420, result.tokens)
        assertEquals(999, result.highScore)
        assertFalse(result.soundEnabled)
        assertFalse(result.musicEnabled)
        assertEquals(3, result.currentLevel)
        assertEquals(3, result.availableBlocks.size)
        assertTrue(result.grid.cells.isEmpty())
        assertFalse(result.isGameOver)
    }
}

