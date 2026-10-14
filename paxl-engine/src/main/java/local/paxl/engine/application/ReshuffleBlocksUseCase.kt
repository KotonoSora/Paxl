package local.paxl.engine.application

import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.repository.BlockCatalog

/**
 * Deals a fresh tray of blocks. Coin-direct: the caller spends coins via core
 * `CoinAccount` before invoking.
 */
class ReshuffleBlocksUseCase(
    private val blockCatalog: BlockCatalog
) {
    operator fun invoke(currentState: GameState): GameState {
        return currentState.copy(
            availableBlocks = blockCatalog.randomBlocks(3)
        )
    }
}
