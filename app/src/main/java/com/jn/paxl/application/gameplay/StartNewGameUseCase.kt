package com.jn.paxl.application.gameplay

import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState

class StartNewGameUseCase(
    private val blockCatalog: BlockCatalog,
    private val gridSize: Int = 10
) {
    operator fun invoke(previousState: GameUiState, level: Int = 1): GameUiState {
        return GameUiState(
            grid = GridState(size = gridSize),
            availableBlocks = blockCatalog.randomBlocks(3),
            currentLevel = level,
            score = 0,
            coins = previousState.coins,
            highScore = previousState.highScore,
            soundEnabled = previousState.soundEnabled,
            musicEnabled = previousState.musicEnabled
        )
    }
}

