package com.jn.paxl.viewmodel

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.paxl.engine.GameEngine
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState
import com.jn.paxl.model.ShapeLibrary
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
    private val billingRepository: BillingRepository
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
            ) { coins, highScore, sound, music ->
                _uiState.update {
                    it.copy(
                        coins = coins,
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
        _uiState.update {
            GameUiState(
                grid = GridState(size = 10),
                availableBlocks = ShapeLibrary.getRandomBlocks(3),
                currentLevel = level,
                score = 0,
                coins = it.coins,
                highScore = it.highScore,
                soundEnabled = it.soundEnabled,
                musicEnabled = it.musicEnabled
            )
        }
        gridHistory.clear()
    }

    fun onBlockPlaced(block: Block, gridPosition: Coordinate) {
        val currentState = _uiState.value
        val grid = currentState.grid

        if (!GameEngine.canPlaceBlock(block, gridPosition, grid)) return

        gridHistory.add(grid.cells.toMap())

        val currentCells = grid.cells.toMutableMap()
        block.shape.forEach { offset ->
            val target = Coordinate(gridPosition.x + offset.x, gridPosition.y + offset.y)
            currentCells[target] = block.color
        }

        val (newCells, linesCleared) = GameEngine.clearLines(currentCells, grid.size)

        val newAvailableBlocks = currentState.availableBlocks.filter { it.id != block.id }.let {
            if (it.isEmpty()) ShapeLibrary.getRandomBlocks(3) else it
        }

        val newScore = currentState.score + (block.shape.size * 10) + (linesCleared * 100)

        _uiState.update {
            it.copy(
                grid = it.grid.copy(cells = newCells),
                availableBlocks = newAvailableBlocks,
                score = newScore,
                isGameOver = GameEngine.checkGameOver(newCells, newAvailableBlocks, it.grid.size)
            )
        }

        if (newScore > currentState.highScore) {
            viewModelScope.launch { repository.saveHighScore(newScore) }
        }
    }

    fun undoMove() {
        if (gridHistory.isNotEmpty() && _uiState.value.coins >= 10) {
            val lastGrid = gridHistory.removeAt(gridHistory.size - 1)
            val newCoins = _uiState.value.coins - 10
            _uiState.update {
                it.copy(grid = it.grid.copy(cells = lastGrid), coins = newCoins)
            }
            viewModelScope.launch { repository.saveCoins(newCoins) }
        }
    }

    fun reshuffleBlocks() {
        if (_uiState.value.coins >= 25) {
            val newCoins = _uiState.value.coins - 25
            _uiState.update {
                it.copy(availableBlocks = ShapeLibrary.getRandomBlocks(3), coins = newCoins)
            }
            viewModelScope.launch { repository.saveCoins(newCoins) }
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
