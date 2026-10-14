package local.paxl.engine.application

import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GameState

/**
 * Restores the latest grid snapshot. Coin-direct: the caller spends coins via
 * core `CoinAccount` before invoking; this use case only validates history.
 * Returns null when there is nothing to undo.
 */
class UndoMoveUseCase {
    operator fun invoke(
        currentState: GameState,
        history: MutableList<Map<Coordinate, Long?>>
    ): GameState? {
        if (history.isEmpty()) return null

        val lastGrid = history.removeAt(history.lastIndex)
        return currentState.copy(
            grid = currentState.grid.copy(cells = lastGrid)
        )
    }
}
