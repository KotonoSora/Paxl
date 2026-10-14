package local.paxl.engine.application

import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.model.LineClearInfo

/**
 * Outcome of a placement: the next aggregate plus what cleared.
 * Callers needing the pre-move state (e.g. undo history) snapshot the
 * [GameState] they passed in — the result carries no redundant copies.
 */
data class PlaceBlockResult(
    val newState: GameState,
    val clearInfo: LineClearInfo? = null
)
