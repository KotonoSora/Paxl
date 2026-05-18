package com.jn.paxl.application.gameplay

import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState

class UndoMoveUseCase(private val undoCost: Int = 10) {
    operator fun invoke(
        currentState: GameUiState,
        history: MutableList<Map<Coordinate, androidx.compose.ui.graphics.Color?>>
    ): GameUiState {
        if (history.isEmpty() || currentState.coins < undoCost) return currentState

        val lastGrid = history.removeAt(history.lastIndex)
        return currentState.copy(
            grid = currentState.grid.copy(cells = lastGrid),
            coins = currentState.coins - undoCost
        )
    }
}

