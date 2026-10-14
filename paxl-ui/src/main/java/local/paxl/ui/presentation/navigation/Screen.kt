package local.paxl.ui.presentation.navigation

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
