package local.paxl.ui.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.paxl.ui.infrastructure.audio.LocalSoundManager
import local.paxl.ui.presentation.components.GameBackHeader
import local.paxl.ui.presentation.components.GameScreenScaffold
import local.paxl.ui.presentation.components.NeonText
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.NeonGreen
import local.paxl.ui.presentation.theme.NeonPink
import local.paxl.ui.presentation.theme.SurfaceDark
import local.paxl.ui.presentation.viewmodel.AudioState
import local.paxl.ui.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val settings by viewModel.settings.collectAsState()

    SettingsScreenContent(
        settings = settings,
        onBack = onBack,
        onToggleSound = { viewModel.toggleSound(it) },
        onToggleMusic = { viewModel.toggleMusic(it) })
}

@Composable
fun SettingsScreenContent(
    settings: AudioState,
    onBack: () -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit
) {
    val soundManager = LocalSoundManager.current

    GameScreenScaffold {
        GameBackHeader(
            title = "SETTINGS",
            titleColor = NeonPink,
            titleFontSize = 24,
            onBack = {
                soundManager.playClick()
                onBack()
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsToggle(
                label = "SOUND EFFECTS", enabled = settings.soundEnabled, onToggle = {
                    onToggleSound(it)
                    soundManager.playClick()
                }, color = NeonCyan
            )

            SettingsToggle(
                label = "MUSIC", enabled = settings.musicEnabled, onToggle = {
                    onToggleMusic(it)
                    soundManager.playClick()
                }, color = NeonGreen
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    GameTheme {
        SettingsScreenContent(
            settings = AudioState(soundEnabled = true, musicEnabled = false),
            onBack = {},
            onToggleSound = {},
            onToggleMusic = {})
    }
}

@Composable
fun SettingsToggle(
    label: String, enabled: Boolean, onToggle: (Boolean) -> Unit, color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonText(
            text = label,
            color = Color.White,
            fontSize = 20,
        )
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = color,
                checkedTrackColor = color.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = SurfaceDark,
            ),
        )
    }
}
