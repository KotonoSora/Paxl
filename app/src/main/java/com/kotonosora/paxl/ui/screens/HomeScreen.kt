package com.kotonosora.paxl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kotonosora.paxl.ui.components.NeonButton
import com.kotonosora.paxl.ui.components.NeonText
import com.kotonosora.paxl.ui.components.NeonTitle
import com.kotonosora.paxl.ui.theme.BackgroundDark
import com.kotonosora.paxl.ui.theme.NeonBlue
import com.kotonosora.paxl.ui.theme.NeonCyan
import com.kotonosora.paxl.ui.theme.NeonGreen
import com.kotonosora.paxl.ui.theme.NeonPink
import com.kotonosora.paxl.ui.theme.NeonYellow
import com.kotonosora.paxl.viewmodel.GameViewModel

@Composable
fun HomeScreen(
    viewModel: GameViewModel = viewModel(),
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShopClick: () -> Unit,
    onDailyChallengeClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top section for coins and shop
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = "Coins",
                    tint = NeonYellow,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                NeonText(
                    text = "${uiState.coins}",
                    color = Color.White,
                    fontSize = 24
                )
            }
            Spacer(Modifier.width(24.dp))
            IconButton(
                onClick = onShopClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    "Shop",
                    tint = NeonCyan,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-8).dp)
            ) {
                NeonTitle("PAXL", color = NeonCyan, fontSize = 64)
                NeonTitle("BLAST", color = NeonPink, fontSize = 64)
            }

            Spacer(Modifier.height(60.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NeonButton(
                    text = "PLAY GAME",
                    color = NeonGreen,
                    onClick = onPlayClick,
                    icon = Icons.Default.Gamepad
                )

                NeonButton(
                    text = "DAILY CHALLENGE",
                    color = NeonYellow,
                    onClick = onDailyChallengeClick,
                    icon = Icons.Default.CalendarToday
                )

                NeonButton(
                    text = "LEADERBOARD",
                    color = NeonBlue,
                    onClick = onLeaderboardClick,
                    icon = Icons.Default.EmojiEvents,
                )

                NeonButton(
                    text = "HELP",
                    color = NeonCyan,
                    onClick = onHelpClick,
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                )

                NeonButton(
                    text = "SETTINGS",
                    color = NeonPink,
                    onClick = onSettingsClick,
                    icon = Icons.Default.Settings
                )
            }
        }
    }
}
