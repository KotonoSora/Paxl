package com.kotonosora.paxl.ui.screens

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
import com.kotonosora.paxl.ui.components.NeonText
import com.kotonosora.paxl.ui.components.PaxlBackHeader
import com.kotonosora.paxl.ui.components.PaxlScreenScaffold
import com.kotonosora.paxl.ui.theme.NeonCyan
import com.kotonosora.paxl.ui.theme.NeonGreen
import com.kotonosora.paxl.ui.theme.NeonPink
import com.kotonosora.paxl.ui.theme.SurfaceDark
import com.kotonosora.paxl.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    PaxlScreenScaffold {
        PaxlBackHeader(
            title = "SETTINGS",
            titleColor = NeonPink,
            titleFontSize = 28,
            onBack = onBack
        )

        Spacer(Modifier.height(48.dp))

        SettingsToggle(
            label = "SOUND EFFECTS",
            enabled = uiState.soundEnabled,
            onToggle = { viewModel.setSoundEnabled(it) },
            color = NeonCyan
        )

        Spacer(Modifier.height(24.dp))

        SettingsToggle(
            label = "MUSIC",
            enabled = uiState.musicEnabled,
            onToggle = { viewModel.setMusicEnabled(it) },
            color = NeonGreen
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
