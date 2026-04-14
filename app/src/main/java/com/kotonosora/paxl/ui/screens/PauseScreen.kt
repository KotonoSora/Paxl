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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotonosora.paxl.ui.components.NeonButton
import com.kotonosora.paxl.ui.components.NeonTitle
import com.kotonosora.paxl.ui.theme.BackgroundDark
import com.kotonosora.paxl.ui.theme.NeonCyan
import com.kotonosora.paxl.ui.theme.NeonGreen
import com.kotonosora.paxl.ui.theme.NeonPink
import com.kotonosora.paxl.ui.theme.NeonYellow

@Composable
fun PauseScreen(onResume: () -> Unit, onRestart: () -> Unit, onQuit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NeonTitle("PAUSED", color = NeonYellow, fontSize = 56)
            Spacer(Modifier.height(48.dp))

            NeonButton(text = "RESUME", color = NeonGreen, onClick = onResume)
            Spacer(Modifier.height(16.dp))
            NeonButton(text = "RESTART", color = NeonCyan, onClick = onRestart)
            Spacer(Modifier.height(16.dp))
            NeonButton(text = "QUIT", color = NeonPink, onClick = onQuit)
        }
    }
}
