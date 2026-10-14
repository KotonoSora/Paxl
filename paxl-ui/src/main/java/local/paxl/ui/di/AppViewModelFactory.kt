package local.paxl.ui.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import local.paxl.ui.presentation.viewmodel.GameViewModel
import local.paxl.ui.presentation.viewmodel.LeaderboardViewModel
import local.paxl.ui.presentation.viewmodel.SettingsViewModel
import local.paxl.ui.presentation.viewmodel.ShopViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(
                    container.coinAccount,
                    container.recordGameFinishedUseCase,
                    container.startNewGameUseCase,
                    container.placeBlockUseCase,
                    container.undoMoveUseCase,
                    container.reshuffleBlocksUseCase
                ) as T
            }

            modelClass.isAssignableFrom(ShopViewModel::class.java) -> {
                ShopViewModel(
                    container.billingRepository,
                    container.coinAccount
                ) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    container.getAudioSettingsUseCase,
                    container.toggleAudioUseCase
                ) as T
            }

            modelClass.isAssignableFrom(LeaderboardViewModel::class.java) -> {
                LeaderboardViewModel(
                    container.getScoreHistoryUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
