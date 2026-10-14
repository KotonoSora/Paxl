package local.paxl.ui.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import local.core.ui.data.CoinAccount
import local.paxl.engine.application.PlaceBlockUseCase
import local.paxl.engine.application.ReshuffleBlocksUseCase
import local.paxl.engine.application.StartNewGameUseCase
import local.paxl.engine.application.UndoMoveUseCase
import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.model.PlayMode
import local.paxl.engine.domain.model.RunEndCause
import local.paxl.engine.domain.model.RunRecord
import local.paxl.engine.domain.service.DropSnapping
import local.paxl.engine.domain.service.PlacementRules
import local.paxl.shop.domain.service.PowerCosts
import local.paxl.ui.application.RecordGameFinishedUseCase
import local.paxl.ui.presentation.components.DragLogTag

class GameViewModel(
    private val coinAccount: CoinAccount,
    private val recordGameFinishedUseCase: RecordGameFinishedUseCase,
    private val startNewGameUseCase: StartNewGameUseCase,
    private val placeBlockUseCase: PlaceBlockUseCase,
    private val undoMoveUseCase: UndoMoveUseCase,
    private val reshuffleBlocksUseCase: ReshuffleBlocksUseCase
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    val coins: StateFlow<Int> = coinAccount.coinsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )

    private val _lastRunNewBest = MutableStateFlow(false)
    val lastRunNewBest: StateFlow<Boolean> = _lastRunNewBest.asStateFlow()

    private val stateHistory = mutableListOf<GameState>()

    private var sessionStartedAt: Long = System.currentTimeMillis()

    // Coin-direct uses in the current run (recorded into the run row).
    private var sessionUndoUses = 0
    private var sessionReshuffleUses = 0

    init {
        startNewGame()
    }

    fun startNewGame(level: Int = 1, mode: PlayMode = PlayMode.CLASSIC) {
        resetSessionCounters()
        _gameState.value = startNewGameUseCase(level, mode)
        stateHistory.clear()
        _lastRunNewBest.value = false
    }

    /**
     * Places [block] at [gridPosition]. Returns true when the placement was
     * legal (callers play the place sound); invalid drops are ignored.
     * The pre-move state is pushed for undo before applying the move.
     */
    fun onBlockPlaced(block: Block, gridPosition: Coordinate): Boolean {
        val currentState = _gameState.value
        if (currentState.isGameOver) return false

        val placeResult = placeBlockUseCase(currentState, block, gridPosition) ?: return false

        commitPlaceResult(currentState, placeResult.newState, placeResult.clearInfo != null)
        return true
    }

    /**
     * Finger-release drop: resolves the ghost and places it on the SAME live
     * grid object, read synchronously here. The screen only maps the release
     * finger to a pointer cell (fresh gesture state, no board data); the board
     * itself is never taken from a composable closure, so a stale composed
     * grid can no longer resolve a "fit" that the live grid then rejects.
     * A returned origin is guaranteed placeable.
     */
    fun onDropAtPointer(block: Block, pointerCell: Coordinate): Boolean {
        val currentState = _gameState.value
        if (currentState.isGameOver) {
            Log.d(DragLogTag, "place REJECT block=${block.id} reason=game-over")
            return false
        }
        Log.d(
            DragLogTag,
            "place TRY block=${block.id} shape=${block.shape} pointerCell=$pointerCell " +
                "filled=${currentState.grid.filledCount}"
        )
        val preview = DropSnapping.previewDrop(block, pointerCell, currentState.grid)
        val origin = preview.origin
        if (origin == null) {
            Log.d(DragLogTag, "place REJECT block=${block.id} reason=no-fit-near")
            return false
        }

        val placeResult = placeBlockUseCase.placeAtPreview(currentState, preview)
        if (placeResult == null) {
            val inTray = currentState.availableBlocks.any { it.id == block.id }
            val fits = PlacementRules.canPlaceBlock(block, origin, currentState.grid)
            Log.d(
                DragLogTag,
                "place REJECT block=${block.id} origin=$origin " +
                    "reason=${if (!inTray) "not-in-tray" else if (!fits) "no-fit" else "unknown"}"
            )
            return false
        }

        commitPlaceResult(currentState, placeResult.newState, placeResult.clearInfo != null)
        Log.d(
            DragLogTag,
            "place OK block=${block.id} origin=$origin " +
                "lines=${placeResult.clearInfo?.linesCleared ?: 0} score=${placeResult.newState.score}"
        )
        return true
    }

    private fun commitPlaceResult(
        currentState: GameState,
        newState: GameState,
        didClear: Boolean
    ) {
        stateHistory.add(currentState)

        var finalState = newState
        val reachedWinTarget =
            !finalState.isWinConditionSkipped && finalState.score >= finalState.targetScore
        if (reachedWinTarget) {
            finalState = finalState.copy(isGameOver = true, isWin = true)
        }

        _gameState.value = finalState

        if (finalState.isGameOver) {
            endGame(finalState.isWin)
        }

        if (didClear) {
            val animationId = newState.clearAnimationId
            viewModelScope.launch {
                delay(1000)
                _gameState.update { state ->
                    if (!state.isClearing || state.clearAnimationId != animationId) return@update state
                    state.copy(
                        isClearing = false,
                        clearingCells = emptyMap()
                    )
                }
            }
        }
    }

    fun continuePlayAfterGameOver() {
        val currentState = _gameState.value
        if (!currentState.isGameOver) return

        if (currentState.isWin) {
            // Free continue, skip further win check.
            _gameState.value = currentState.copy(
                isGameOver = false,
                isWin = false,
                isWinConditionSkipped = true,
                isClearing = false,
                clearAnimationId = 0L,
                clearingCells = emptyMap()
            )
            return
        }

        viewModelScope.launch {
            if (!coinAccount.spendCoins(PowerCosts.CONTINUE_COST)) return@launch
            sessionUndoUses = 0
            sessionReshuffleUses = 0
            _gameState.value = startNewGameUseCase(
                currentState.currentLevel,
                currentState.playMode
            ).copy(
                score = currentState.score,
                linesCleared = currentState.linesCleared,
                sessionStartMs = currentState.sessionStartMs
            )
            stateHistory.clear()
        }
    }

    fun undoMove() {
        if (_gameState.value.isGameOver || stateHistory.isEmpty()) return
        viewModelScope.launch {
            if (coinAccount.spendCoins(PowerCosts.UNDO_COST)) {
                val updated = undoMoveUseCase(stateHistory) ?: return@launch
                sessionUndoUses++
                _gameState.value = updated
            }
        }
    }

    fun reshuffleBlocks() {
        if (_gameState.value.isGameOver) return
        viewModelScope.launch {
            if (coinAccount.spendCoins(PowerCosts.RESHUFFLE_COST)) {
                sessionReshuffleUses++
                _gameState.value = reshuffleBlocksUseCase(_gameState.value)
            }
        }
    }

    private fun resetSessionCounters() {
        sessionStartedAt = System.currentTimeMillis()
        sessionUndoUses = 0
        sessionReshuffleUses = 0
    }

    private fun endGame(won: Boolean) {
        val endedAt = System.currentTimeMillis()
        viewModelScope.launch {
            val state = _gameState.value
            _lastRunNewBest.value = recordGameFinishedUseCase(
                RunRecord(
                    mode = state.playMode.name,
                    level = state.currentLevel,
                    score = state.score,
                    reward = if (won) state.winReward else 0,
                    durationMs = (endedAt - sessionStartedAt).coerceAtLeast(0L),
                    // Repurposed core field: lines cleared (see engine RunRecord KDoc).
                    pipesPassed = state.linesCleared,
                    endCause = if (won) RunEndCause.WIN else RunEndCause.GAME_OVER,
                    shieldUses = sessionUndoUses,
                    multiplierUses = sessionReshuffleUses,
                    startedAt = sessionStartedAt,
                    endedAt = endedAt
                )
            )
        }
    }
}
