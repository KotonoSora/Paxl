package com.jn.paxl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.paxl.model.GameUiState
import com.jn.paxl.ui.LocalSoundManager
import com.jn.paxl.ui.components.NeonButton
import com.jn.paxl.ui.components.NeonText
import com.jn.paxl.ui.components.NeonTitle
import com.jn.paxl.ui.theme.BackgroundDark
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.NeonGreen
import com.jn.paxl.ui.theme.NeonPink
import com.jn.paxl.ui.theme.NeonYellow
import com.jn.paxl.viewmodel.GameViewModel

@Composable
fun ResultScreen(
    viewModel: GameViewModel, onContinue: () -> Unit, onPlayAgain: () -> Unit, onHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ResultScreenContent(
        uiState = uiState, onContinue = onContinue, onPlayAgain = onPlayAgain, onHome = onHome
    )
}

@Composable
fun ResultScreenContent(
    uiState: GameUiState, onContinue: () -> Unit, onPlayAgain: () -> Unit, onHome: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    val canContinue = uiState.isWin || uiState.tokens >= uiState.continueTokenCost

    LaunchedEffect(uiState.isWin) {
        if (uiState.isWin) {
            soundManager?.playWin()
        } else {
            soundManager?.playLose()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NeonTitle(
                if (uiState.isWin) "YOU WIN" else "GAME OVER",
                color = if (uiState.isWin) NeonGreen else NeonPink,
                fontSize = 56
            )
            Spacer(Modifier.height(24.dp))
            NeonText(
                "SCORE\n${uiState.score}",
                color = Color.White,
                fontSize = 32,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            if (uiState.isWin) {
                NeonText(
                    "REWARD\n+${uiState.winTokenReward} TOKENS", color = NeonYellow, fontSize = 20,
                    textAlign = TextAlign.Center,
                )
            }

            if (canContinue && !uiState.isWin) {
                NeonText(
                    text = "CONTINUE COST\n${uiState.continueTokenCost} TOKENS",
                    color = NeonYellow,
                    fontSize = 16,
                    textAlign = TextAlign.Center,
                )
            }
            if (canContinue) {
                Spacer(Modifier.height(24.dp))
                NeonButton(
                    text = if (uiState.isWin) "CONTINUE PLAY" else "CONTINUE",
                    color = NeonYellow,
                    enabled = canContinue,
                    onClick = {
                        soundManager?.playClick()
                        onContinue()
                    })
                Spacer(Modifier.height(16.dp))
            }
            NeonButton(
                text = "PLAY AGAIN", color = NeonGreen, onClick = {
                    soundManager?.playClick()
                    onPlayAgain()
                })
            Spacer(Modifier.height(16.dp))
            NeonButton(
                text = "HOME", color = NeonCyan, onClick = {
                    soundManager?.playClick()
                    onHome()
                })
        }
    }
}

@Preview(showBackground = true, name = "Lose - Can Continue")
@Composable
fun ResultScreenLoseCanContinuePreview() {
    GameTheme {
        ResultScreenContent(
            uiState = GameUiState(
                score = 1234, isWin = false, tokens = 80, continueTokenCost = 30
            ),
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
            uiState = GameUiState(
                score = 980, isWin = false, tokens = 10, continueTokenCost = 30
            ),
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
            uiState = GameUiState(
                score = 2450, isWin = true, tokens = 120, winTokenReward = 25
            ),
            onContinue = {},
            onPlayAgain = {},
            onHome = {},
        )
    }
}
