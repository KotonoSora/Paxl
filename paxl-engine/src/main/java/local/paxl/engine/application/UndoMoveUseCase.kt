package local.paxl.engine.application

import local.paxl.engine.domain.model.GameState

/**
 * Pops the latest pre-move snapshot, restoring the full run state —
 * board, tray, score and combo alike. Coin-direct: the caller spends coins
 * via core `CoinAccount` before invoking; this use case only validates
 * history. Returns null when there is nothing to undo.
 */
class UndoMoveUseCase {
    operator fun invoke(history: MutableList<GameState>): GameState? {
        if (history.isEmpty()) return null
        return history.removeAt(history.lastIndex)
    }
}
