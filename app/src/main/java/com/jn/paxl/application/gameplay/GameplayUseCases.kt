package com.jn.paxl.application.gameplay

/**
 * Aggregates gameplay use cases so the ViewModel can receive deterministic test doubles.
 */
data class GameplayUseCases(
    val startNewGame: StartNewGameUseCase,
    val placeBlock: PlaceBlockUseCase,
    val undoMove: UndoMoveUseCase,
    val reshuffleBlocks: ReshuffleBlocksUseCase
)

