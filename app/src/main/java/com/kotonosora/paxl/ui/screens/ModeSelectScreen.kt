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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kotonosora.paxl.ui.components.NeonButton
import com.kotonosora.paxl.ui.components.NeonTitle
import com.kotonosora.paxl.ui.theme.BackgroundDark
import com.kotonosora.paxl.ui.theme.NeonCyan
import com.kotonosora.paxl.ui.theme.NeonGreen
import com.kotonosora.paxl.ui.theme.NeonYellow

@Composable
fun ModeSelectScreen(
    onModeSelected: () -> Unit,
    onLevelSelectClick: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NeonTitle("SELECT MODE", color = NeonCyan, fontSize = 40)
            Spacer(Modifier.height(48.dp))

            NeonButton(
                text = "CLASSIC",
                color = NeonGreen,
                onClick = onModeSelected,
                icon = Icons.Default.PlayArrow
            )
            Spacer(Modifier.height(16.dp))
            NeonButton(
                text = "LEVELS",
                color = NeonYellow,
                onClick = onLevelSelectClick,
                icon = Icons.AutoMirrored.Filled.FormatListBulleted
            )
            Spacer(Modifier.height(16.dp))
            NeonButton(
                text = "BACK",
                color = Color.Gray,
                onClick = onBack
            )
        }
    }
}
