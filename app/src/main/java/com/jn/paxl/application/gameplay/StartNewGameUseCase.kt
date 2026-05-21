package com.jn.paxl.application.gameplay

import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState
import com.jn.paxl.model.PlayMode

class StartNewGameUseCase(
    private val blockCatalog: BlockCatalog,
    private val gridSize: Int = 10
) {
    operator fun invoke(
        previousState: GameUiState,
        level: Int = 1,
        mode: PlayMode = PlayMode.CLASSIC
    ): GameUiState {
        val (targetScore, winTokenReward) = when (mode) {
            PlayMode.CLASSIC -> 5000 to 25
            PlayMode.LEVELS -> (700 + (level * 350)) to (10 + (level * 2))
            PlayMode.DAILY -> 2200 to 40
        }

        return GameUiState(
            grid = GridState(size = gridSize),
            availableBlocks = blockCatalog.randomBlocks(3),
            currentLevel = level,
            playMode = mode,
            targetScore = targetScore,
            winTokenReward = winTokenReward,
            sessionStartMs = System.currentTimeMillis(),
            score = 0,
            tokens = previousState.tokens,
            isWin = false,
            isWinConditionSkipped = false,
            highScore = previousState.highScore,
            soundEnabled = previousState.soundEnabled,
            musicEnabled = previousState.musicEnabled
        )
    }
}

