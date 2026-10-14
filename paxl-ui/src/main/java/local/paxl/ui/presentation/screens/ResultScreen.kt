package local.paxl.ui.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.paxl.engine.domain.model.GameState
import local.paxl.shop.domain.service.PowerCosts
import local.paxl.ui.infrastructure.audio.LocalSoundManager
import local.paxl.ui.presentation.components.NeonButton
import local.paxl.ui.presentation.components.NeonText
import local.paxl.ui.presentation.components.NeonTitle
import local.paxl.ui.presentation.theme.BackgroundDark
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.NeonGreen
import local.paxl.ui.presentation.theme.NeonPink
import local.paxl.ui.presentation.theme.NeonYellow

@Composable
fun ResultScreen(
    gameState: GameState,
    coins: Int,
    isNewBest: Boolean,
    onContinue: () -> Unit,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    ResultScreenContent(
        gameState = gameState,
        coins = coins,
        isNewBest = isNewBest,
        onContinue = onContinue,
        onPlayAgain = onPlayAgain,
        onHome = onHome
    )
}

@Composable
fun ResultScreenContent(
    gameState: GameState,
    coins: Int,
    isNewBest: Boolean,
    onContinue: () -> Unit,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    val canContinue = gameState.isWin || coins >= PowerCosts.CONTINUE_COST

    LaunchedEffect(gameState.isWin) {
        if (gameState.isWin) {
            soundManager.playWin()
        } else {
            soundManager.playLose()
        }
    }

    Scaffold(containerColor = BackgroundDark.copy(alpha = 0.9f)) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundDark.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                NeonTitle(
                    if (gameState.isWin) "YOU WIN" else "GAME OVER",
                    color = if (gameState.isWin) NeonGreen else NeonPink,
                    fontSize = 56
                )
                Spacer(Modifier.height(24.dp))
                NeonText(
                    "SCORE\n${gameState.score}",
                    color = Color.White,
                    fontSize = 32,
                    textAlign = TextAlign.Center,
                )
                if (isNewBest) {
                    Spacer(Modifier.height(8.dp))
                    NeonText(
                        "NEW BEST!",
                        color = NeonGreen,
                        fontSize = 16,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(24.dp))
                if (gameState.isWin) {
                    NeonText(
                        "REWARD\n+${gameState.winReward} TOKENS", color = NeonYellow, fontSize = 20,
                        textAlign = TextAlign.Center,
                    )
                }

                if (canContinue && !gameState.isWin) {
                    NeonText(
                        text = "CONTINUE COST\n${PowerCosts.CONTINUE_COST} TOKENS",
                        color = NeonYellow,
                        fontSize = 16,
                        textAlign = TextAlign.Center,
                    )
                }
                if (canContinue) {
                    Spacer(Modifier.height(24.dp))
                    NeonButton(
                        text = if (gameState.isWin) "CONTINUE PLAY" else "CONTINUE",
                        color = NeonYellow,
                        enabled = canContinue,
                        onClick = {
                            soundManager.playClick()
                            onContinue()
                        })
                    Spacer(Modifier.height(16.dp))
                }
                NeonButton(
                    text = "PLAY AGAIN", color = NeonGreen, onClick = {
                        soundManager.playClick()
                        onPlayAgain()
                    })
                Spacer(Modifier.height(16.dp))
                NeonButton(
                    text = "HOME", color = NeonCyan, onClick = {
                        soundManager.playClick()
                        onHome()
                    })
            }
        }
    }
}

@Preview(showBackground = true, name = "Lose - Can Continue")
@Composable
fun ResultScreenLoseCanContinuePreview() {
    GameTheme {
        ResultScreenContent(
            gameState = GameState(score = 1234, isWin = false),
            coins = 80,
            isNewBest = false,
            onContinue = {},
            onPlayAgain = {},
            onHome = {},
        )
    }
}

@Preview(showBackground = true, name = "Lose - Cannot Continue")
@Composable
fun ResultScreenLoseCannotContinuePreview() {
    GameTheme {
        ResultScreenContent(
            gameState = GameState(score = 980, isWin = false),
            coins = 10,
            isNewBest = false,
            onContinue = {},
            onPlayAgain = {},
            onHome = {},
        )
    }
}

@Preview(showBackground = true, name = "Win")
@Composable
fun ResultScreenWinPreview() {
    GameTheme {
        ResultScreenContent(
            gameState = GameState(score = 2450, isWin = true, winReward = 25),
            coins = 120,
            isNewBest = true,
            onContinue = {},
            onPlayAgain = {},
            onHome = {},
        )
    }
}
