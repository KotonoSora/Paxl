package com.jn.paxl.viewmodel

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.paxl.application.gameplay.GameplayUseCases
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.LeaderboardEntry
import com.jn.paxl.model.PlayMode
import com.jn.paxl.repository.BillingRepository
import com.jn.paxl.repository.DataStoreRepository
import com.jn.paxl.repository.StoreProduct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: DataStoreRepository,
    private val billingRepository: BillingRepository,
    private val gameplayUseCases: GameplayUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    val shopProducts: StateFlow<List<StoreProduct>> = billingRepository.products

    private val gridHistory = mutableListOf<Map<Coordinate, Color?>>()

    init {
        // Load persisted data
        viewModelScope.launch {
            combine(
                repository.coinsFlow,
                repository.highScoreFlow,
                repository.soundEnabledFlow,
                repository.musicEnabledFlow
            ) { tokens, highScore, sound, music ->
                _uiState.update {
                    it.copy(
                        tokens = tokens,
                        highScore = highScore,
                        soundEnabled = sound,
                        musicEnabled = music
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            repository.leaderboardFlow.collect { entries ->
                _leaderboard.value = sortLeaderboard(entries)
            }
        }

        startNewGame()
    }

    fun startNewGame(level: Int = 1, mode: PlayMode = PlayMode.CLASSIC) {
        _uiState.update { state -> gameplayUseCases.startNewGame(state, level, mode) }
        gridHistory.clear()
    }

    fun onBlockPlaced(block: Block, gridPosition: Coordinate) {
        val currentState = _uiState.value
        if (currentState.isGameOver) return

        val placeResult = gameplayUseCases.placeBlock(currentState, block, gridPosition) ?: return

        gridHistory.add(placeResult.previousGridSnapshot)

        var finalState = placeResult.newState
        val reachedWinTarget = !finalState.isWinConditionSkipped && finalState.score >= finalState.targetScore
        if (reachedWinTarget) {
            finalState = finalState.copy(
                isGameOver = true,
                isWin = true,
                tokens = finalState.tokens + finalState.winTokenReward
            )
        }

        _uiState.value = finalState

        if (finalState.score > currentState.highScore) {
            viewModelScope.launch { repository.saveHighScore(finalState.score) }
        }

        if (finalState.isWin && finalState.tokens != currentState.tokens) {
            viewModelScope.launch { repository.saveCoins(finalState.tokens) }
        }

        if (finalState.isGameOver) {
            val now = System.currentTimeMillis()
            val elapsedSeconds = ((now - finalState.sessionStartMs) / 1000L).coerceAtLeast(0L)
            val entry = LeaderboardEntry(
                score = finalState.score,
                durationSeconds = elapsedSeconds,
                recordedAtEpochMs = now
            )
            viewModelScope.launch {
                repository.saveLeaderboardEntry(entry)
            }
        }

        if (placeResult.clearInfo != null) {
            val animationId = placeResult.newState.clearAnimationId
            viewModelScope.launch {
                delay(1000)
                _uiState.update { state ->
                    if (!state.isClearing || state.clearAnimationId != animationId) return@update state
                    state.copy(
                        isClearing = false,
                        clearAnimationId = state.clearAnimationId,
                        clearingCells = emptyMap()
                    )
                }
            }
        }
    }

    fun continuePlayAfterGameOver() {
        val currentState = _uiState.value
        if (!currentState.isGameOver) return

        if (currentState.isWin) {
            _uiState.value = currentState.copy(
                isGameOver = false,
                isWin = false,
                isWinConditionSkipped = true,
                isClearing = false,
                clearAnimationId = 0L,
                clearingCells = emptyMap()
            )
            return
        }

        if (currentState.tokens < currentState.continueTokenCost) return

        val continuedState = gameplayUseCases.startNewGame(
            previousState = currentState,
            level = currentState.currentLevel,
            mode = currentState.playMode
        ).copy(
            score = currentState.score,
            tokens = currentState.tokens - currentState.continueTokenCost,
            sessionStartMs = currentState.sessionStartMs,
            isGameOver = false,
            isWin = false,
            isWinConditionSkipped = false,
            isClearing = false,
            clearAnimationId = 0L,
            clearingCells = emptyMap()
        )

        gridHistory.clear()
        _uiState.value = continuedState
        viewModelScope.launch { repository.saveCoins(continuedState.tokens) }
    }

    fun undoMove() {
        val updatedState = gameplayUseCases.undoMove(_uiState.value, gridHistory)
        if (updatedState.tokens != _uiState.value.tokens) {
            _uiState.value = updatedState
            viewModelScope.launch { repository.saveCoins(updatedState.tokens) }
        }
    }

    fun reshuffleBlocks() {
        val updatedState = gameplayUseCases.reshuffleBlocks(_uiState.value)
        if (updatedState.tokens != _uiState.value.tokens) {
            _uiState.value = updatedState
            viewModelScope.launch { repository.saveCoins(updatedState.tokens) }
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.saveSoundEnabled(enabled) }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.saveMusicEnabled(enabled) }
    }

    fun purchaseCoins(activity: Activity, product: StoreProduct) {
        billingRepository.launchBillingFlow(activity, product)
    }

    override fun onCleared() {
        super.onCleared()
        billingRepository.endConnection()
    }

    private fun sortLeaderboard(entries: List<LeaderboardEntry>): List<LeaderboardEntry> {
        return entries.sortedWith(
            compareByDescending<LeaderboardEntry> { it.score }
                .thenBy { it.durationSeconds }
                .thenByDescending { it.recordedAtEpochMs }
        )
    }
}
