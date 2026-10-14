package local.paxl.engine.application

import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.model.LineClearInfo

data class PlaceBlockResult(
    val newState: GameState,
    val previousGridSnapshot: Map<Coordinate, Long?>,
    val clearInfo: LineClearInfo? = null
)
