package com.jn.paxl.application.gameplay

import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.GameUiState

class ReshuffleBlocksUseCase(
    private val blockCatalog: BlockCatalog,
    private val reshuffleCost: Int = 25
) {
    operator fun invoke(currentState: GameUiState): GameUiState {
        if (currentState.coins < reshuffleCost) return currentState

        return currentState.copy(
            availableBlocks = blockCatalog.randomBlocks(3),
            coins = currentState.coins - reshuffleCost
        )
    }
}

