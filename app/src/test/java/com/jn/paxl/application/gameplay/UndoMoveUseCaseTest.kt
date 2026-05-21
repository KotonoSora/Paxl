package com.jn.paxl.application.gameplay

import androidx.compose.ui.graphics.Color
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class UndoMoveUseCaseTest {

    @Test
    fun `undo returns same state when history is empty`() {
        val useCase = UndoMoveUseCase(undoCost = 10)
        val state = GameUiState(tokens = 100)

        val result = useCase(state, mutableListOf())

        assertSame(state, result)
    }

    @Test
    fun `undo returns same state when tokens are insufficient`() {
        val useCase = UndoMoveUseCase(undoCost = 10)
        val history = mutableListOf<Map<Coordinate, Color?>>(emptyMap())
        val state = GameUiState(tokens = 5)

        val result = useCase(state, history)

        assertSame(state, result)
        assertEquals(1, history.size)
    }

    @Test
    fun `undo restores previous grid and consumes latest history snapshot`() {
        val useCase = UndoMoveUseCase(undoCost = 10)
        val oldest: Map<Coordinate, Color?> = mapOf(Coordinate(0, 0) to Color.Red)
        val newest: Map<Coordinate, Color?> = mapOf(Coordinate(1, 1) to Color.Green)
        val history = mutableListOf(oldest, newest)
        val state = GameUiState(
            grid = GridState(cells = mapOf(Coordinate(2, 2) to Color.Blue), size = 10),
            tokens = 50
        )

        val result = useCase(state, history)

        assertEquals(40, result.tokens)
        assertEquals(newest, result.grid.cells)
        assertEquals(1, history.size)
        assertTrue(history.contains(oldest))
    }
}
