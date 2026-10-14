package local.paxl.ui.presentation.viewmodel

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
import local.paxl.shop.domain.service.PowerCosts
import local.paxl.ui.application.RecordGameFinishedUseCase

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

    private val gridHistory = mutableListOf<Map<Coordinate, Long?>>()

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
        gridHistory.clear()
        _lastRunNewBest.value = false
    }

    /**
     * Places [block] at [gridPosition]. Returns true when the placement was
     * legal (callers play the place sound); invalid drops are ignored.
     */
    fun onBlockPlaced(block: Block, gridPosition: Coordinate): Boolean {
        val currentState = _gameState.value
        if (currentState.isGameOver) return false

        val placeResult = placeBlockUseCase(currentState, block, gridPosition) ?: return false

        gridHistory.add(placeResult.previousGridSnapshot)

        var finalState = placeResult.newState
        val reachedWinTarget =
            !finalState.isWinConditionSkipped && finalState.score >= finalState.targetScore
        if (reachedWinTarget) {
            finalState = finalState.copy(isGameOver = true, isWin = true)
        }

        _gameState.value = finalState

        if (finalState.isGameOver) {
            endGame(finalState.isWin)
        }

        if (placeResult.clearInfo != null) {
            val animationId = placeResult.newState.clearAnimationId
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
        return true
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
            gridHistory.clear()
        }
    }

    fun undoMove() {
        if (_gameState.value.isGameOver || gridHistory.isEmpty()) return
        viewModelScope.launch {
            if (coinAccount.spendCoins(PowerCosts.UNDO_COST)) {
                val updated = undoMoveUseCase(_gameState.value, gridHistory) ?: return@launch
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
