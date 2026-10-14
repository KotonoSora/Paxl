package local.paxl.ui.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import local.paxl.engine.domain.model.PlayMode
import local.paxl.ui.di.AppViewModelFactory
import local.paxl.ui.di.LocalAppContainer
import local.paxl.ui.infrastructure.audio.LocalSoundManager
import local.paxl.ui.presentation.navigation.Screen
import local.paxl.ui.presentation.screens.CoinShopScreen
import local.paxl.ui.presentation.screens.DailyChallengeScreen
import local.paxl.ui.presentation.screens.GamePlayScreen
import local.paxl.ui.presentation.screens.HelpScreen
import local.paxl.ui.presentation.screens.HomeScreen
import local.paxl.ui.presentation.screens.LeaderboardScreen
import local.paxl.ui.presentation.screens.LevelSelectScreen
import local.paxl.ui.presentation.screens.ModeSelectScreen
import local.paxl.ui.presentation.screens.PauseScreen
import local.paxl.ui.presentation.screens.ResultScreen
import local.paxl.ui.presentation.screens.SettingsScreen
import local.paxl.ui.presentation.viewmodel.GameViewModel
import local.paxl.ui.presentation.viewmodel.LeaderboardViewModel
import local.paxl.ui.presentation.viewmodel.SettingsViewModel
import local.paxl.ui.presentation.viewmodel.ShopViewModel

@Composable
fun MainApp(
    factory: AppViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    val soundManager = LocalSoundManager.current
    // Sanctioned SFX-only wiring: the SFX toggle flows from core AudioSettings;
    // Settings routes toggles through core (see SettingsViewModel).
    val sfxEnabled by LocalAppContainer.current.audioSettings.sfxEnabledFlow
        .collectAsState(initial = true)
    soundManager.soundEnabled = sfxEnabled

    val gameViewModel: GameViewModel = viewModel(factory = factory)
    val shopViewModel: ShopViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val leaderboardViewModel: LeaderboardViewModel = viewModel(factory = factory)

    val coins by gameViewModel.coins.collectAsState()
    val gameState by gameViewModel.gameState.collectAsState()
    val lastRunNewBest by gameViewModel.lastRunNewBest.collectAsState()

    val navigateHomeAsRoot: () -> Unit = {
        navController.navigate(Screen.Home.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    val navigateBackSafely: () -> Unit = {
        if (!navController.popBackStack(Screen.Home.route, inclusive = false)) {
            navigateHomeAsRoot()
        }
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                coins = coins,
                onPlayClick = { navController.navigate(Screen.ModeSelect.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onShopClick = { navController.navigate(Screen.CoinShop.route) },
                onDailyChallengeClick = { navController.navigate(Screen.DailyChallenge.route) },
                onLeaderboardClick = { navController.navigate(Screen.Leaderboard.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) }
            )
        }
        composable(Screen.ModeSelect.route) {
            ModeSelectScreen(
                onModeSelected = {
                    gameViewModel.startNewGame(mode = PlayMode.CLASSIC)
                    navController.navigate(Screen.GamePlay.route)
                },
                onLevelSelectClick = { navController.navigate(Screen.LevelSelect.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.LevelSelect.route) {
            LevelSelectScreen(
                onLevelSelected = { selectedLevel ->
                    gameViewModel.startNewGame(level = selectedLevel, mode = PlayMode.LEVELS)
                    navController.navigate(Screen.GamePlay.route)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.GamePlay.route) {
            GamePlayScreen(
                viewModel = gameViewModel,
                coins = coins,
                onPauseClick = { navController.navigate(Screen.Pause.route) },
                onGameOver = { navController.navigate(Screen.Result.route) },
                onShopClick = {
                    soundManager.playClick()
                    navController.navigate(Screen.CoinShop.route)
                }
            )
        }
        composable(Screen.Pause.route) {
            PauseScreen(
                onResume = { navController.popBackStack() },
                onRestart = {
                    gameViewModel.startNewGame()
                    navController.popBackStack()
                },
                onQuit = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Result.route) {
            ResultScreen(
                gameState = gameState,
                coins = coins,
                isNewBest = lastRunNewBest,
                onContinue = {
                    gameViewModel.continuePlayAfterGameOver()
                    navController.popBackStack()
                },
                onPlayAgain = {
                    gameViewModel.startNewGame()
                    navController.navigate(Screen.GamePlay.route) {
                        popUpTo(Screen.GamePlay.route) { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.CoinShop.route) {
            CoinShopScreen(viewModel = shopViewModel, onBack = { navigateBackSafely() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = settingsViewModel, onBack = { navigateBackSafely() })
        }
        composable(Screen.Help.route) {
            HelpScreen(onBack = { navigateBackSafely() })
        }
        composable(Screen.DailyChallenge.route) {
            DailyChallengeScreen(
                onBack = { navigateBackSafely() },
                onStartChallenge = {
                    gameViewModel.startNewGame(mode = PlayMode.DAILY)
                    navController.navigate(Screen.GamePlay.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                viewModel = leaderboardViewModel,
                onBack = { navigateBackSafely() }
            )
        }
    }
}
