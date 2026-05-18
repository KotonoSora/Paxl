package com.jn.paxl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jn.paxl.ui.LocalSoundManager
import com.jn.paxl.ui.components.NeonText
import com.jn.paxl.ui.components.PaxlBackHeader
import com.jn.paxl.ui.components.PaxlScreenScaffold
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.NeonGreen
import com.jn.paxl.ui.theme.NeonPink
import com.jn.paxl.ui.theme.SurfaceDark
import com.jn.paxl.viewmodel.GameViewModel

import androidx.compose.ui.tooling.preview.Preview
import com.jn.paxl.model.GameUiState
import com.jn.paxl.ui.theme.PaxlTheme

@Composable
fun SettingsScreen(
    viewModel: GameViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    SettingsScreenContent(
        uiState = uiState,
        onBack = onBack,
        onToggleSound = { viewModel.setSoundEnabled(it) },
        onToggleMusic = { viewModel.setMusicEnabled(it) }
    )
}

@Composable
fun SettingsScreenContent(
    uiState: GameUiState,
    onBack: () -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit
) {
    val soundManager = LocalSoundManager.current

    PaxlScreenScaffold {
        PaxlBackHeader(
            title = "SETTINGS",
            titleColor = NeonPink,
            titleFontSize = 28,
            onBack = {
                soundManager?.playClick()
                onBack()
            }
        )

        Spacer(Modifier.height(48.dp))

        SettingsToggle(
            label = "SOUND EFFECTS",
            enabled = uiState.soundEnabled,
            onToggle = {
                onToggleSound(it)
                soundManager?.playClick()
            },
            color = NeonCyan
        )

        Spacer(Modifier.height(24.dp))

        SettingsToggle(
            label = "MUSIC",
            enabled = uiState.musicEnabled,
            onToggle = {
                onToggleMusic(it)
                soundManager?.playClick()
            },
            color = NeonGreen
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PaxlTheme {
        SettingsScreenContent(
            uiState = GameUiState(soundEnabled = true, musicEnabled = false),
            onBack = {},
            onToggleSound = {},
            onToggleMusic = {}
        )
    }
}

@Composable
fun SettingsToggle(
    label: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonText(text = label, color = Color.White, fontSize = 20)
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = color,
                checkedTrackColor = color.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = SurfaceDark
            )
        )
    }
}
