package com.jn.paxl.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jn.paxl.model.PlayMode
import com.jn.paxl.ui.screens.CoinShopScreen
import com.jn.paxl.ui.screens.DailyChallengeScreen
import com.jn.paxl.ui.screens.GamePlayScreen
import com.jn.paxl.ui.screens.HelpScreen
import com.jn.paxl.ui.screens.HomeScreen
import com.jn.paxl.ui.screens.LeaderboardScreen
import com.jn.paxl.ui.screens.LevelSelectScreen
import com.jn.paxl.ui.screens.ModeSelectScreen
import com.jn.paxl.ui.screens.PauseScreen
import com.jn.paxl.ui.screens.ResultScreen
import com.jn.paxl.ui.screens.SettingsScreen
import com.jn.paxl.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object GamePlay : Screen("game_play")
    object ModeSelect : Screen("mode_select")
    object LevelSelect : Screen("level_select")
    object CoinShop : Screen("coin_shop")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object Pause : Screen("pause")
    object Result : Screen("result")
    object DailyChallenge : Screen("daily_challenge")
    object Leaderboard : Screen("leaderboard")
}

@Composable
fun GameNavigation(viewModel: GameViewModel = viewModel()) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
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
                        viewModel.startNewGame(mode = PlayMode.CLASSIC)
                        navController.navigate(Screen.GamePlay.route)
                    },
                    onLevelSelectClick = { navController.navigate(Screen.LevelSelect.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.LevelSelect.route) {
                LevelSelectScreen(
                    onLevelSelected = { selectedLevel ->
                        viewModel.startNewGame(level = selectedLevel, mode = PlayMode.LEVELS)
                        navController.navigate(Screen.GamePlay.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.GamePlay.route) {
                GamePlayScreen(
                    viewModel = viewModel,
                    onPauseClick = { navController.navigate(Screen.Pause.route) },
                    onGameOver = { navController.navigate(Screen.Result.route) }
                )
            }
            composable(Screen.Pause.route) {
                PauseScreen(
                    onResume = { navController.popBackStack() },
                    onRestart = {
                        viewModel.startNewGame()
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
                    viewModel = viewModel,
                    onContinue = {
                        viewModel.continuePlayAfterGameOver()
                        navController.popBackStack()
                    },
                    onPlayAgain = {
                        viewModel.startNewGame()
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
                CoinShopScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable(Screen.Help.route) {
                HelpScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.DailyChallenge.route) {
                DailyChallengeScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onStartChallenge = {
                        viewModel.startNewGame(mode = PlayMode.DAILY)
                        navController.navigate(Screen.GamePlay.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(
                    viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
