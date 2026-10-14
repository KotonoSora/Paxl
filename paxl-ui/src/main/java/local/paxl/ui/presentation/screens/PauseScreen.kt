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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.paxl.ui.presentation.components.NeonButton
import local.paxl.ui.presentation.components.NeonTitle
import local.paxl.ui.presentation.theme.BackgroundDark
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.NeonGreen
import local.paxl.ui.presentation.theme.NeonPink
import local.paxl.ui.presentation.theme.NeonYellow

@Composable
fun PauseScreen(onResume: () -> Unit, onRestart: () -> Unit, onQuit: () -> Unit) {
    Scaffold(containerColor = BackgroundDark.copy(alpha = 0.6f)) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundDark.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                NeonTitle("PAUSED", color = NeonYellow, fontSize = 48)
                Spacer(Modifier.height(48.dp))

                NeonButton(text = "RESUME", color = NeonGreen, onClick = onResume)
                Spacer(Modifier.height(16.dp))
                NeonButton(text = "RESTART", color = NeonCyan, onClick = onRestart)
                Spacer(Modifier.height(16.dp))
                NeonButton(text = "QUIT", color = NeonPink, onClick = onQuit)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PauseScreenPreview() {
    GameTheme {
        PauseScreen(onResume = {}, onRestart = {}, onQuit = {})
    }
}
