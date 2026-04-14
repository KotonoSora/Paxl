package com.kotonosora.paxl.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kotonosora.paxl.ui.components.NeonButton
import com.kotonosora.paxl.ui.components.NeonText
import com.kotonosora.paxl.ui.components.NeonTitle
import com.kotonosora.paxl.ui.theme.BackgroundDark
import com.kotonosora.paxl.ui.theme.NeonCyan
import com.kotonosora.paxl.ui.theme.NeonGreen
import com.kotonosora.paxl.ui.theme.NeonPink
import com.kotonosora.paxl.viewmodel.GameViewModel

@Composable
fun ResultScreen(viewModel: GameViewModel, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

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
            NeonTitle("GAME OVER", color = NeonPink, fontSize = 56)
            Spacer(Modifier.height(24.dp))
            NeonText("SCORE: ${uiState.score}", color = Color.White, fontSize = 32)
            Spacer(Modifier.height(48.dp))

            NeonButton(text = "PLAY AGAIN", color = NeonGreen, onClick = onPlayAgain)
            Spacer(Modifier.height(16.dp))
            NeonButton(text = "HOME", color = NeonCyan, onClick = onHome)
        }
    }
}
