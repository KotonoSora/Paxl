package com.jn.paxl.viewmodel

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.paxl.application.gameplay.GameplayUseCases
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.repository.BillingRepository
import com.jn.paxl.repository.DataStoreRepository
import com.jn.paxl.repository.StoreProduct
import dagger.hilt.android.lifecycle.HiltViewModel
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

        startNewGame()
    }

    fun startNewGame(level: Int = 1) {
        _uiState.update { state -> gameplayUseCases.startNewGame(state, level) }
        gridHistory.clear()
    }

    fun onBlockPlaced(block: Block, gridPosition: Coordinate) {
        val currentState = _uiState.value
        val placeResult = gameplayUseCases.placeBlock(currentState, block, gridPosition) ?: return

        gridHistory.add(placeResult.previousGridSnapshot)
        _uiState.value = placeResult.newState

        if (placeResult.newState.score > currentState.highScore) {
            viewModelScope.launch { repository.saveHighScore(placeResult.newState.score) }
        }
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
}
