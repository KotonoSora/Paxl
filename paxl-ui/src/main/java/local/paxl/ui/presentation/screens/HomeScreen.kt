package local.paxl.ui.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import local.paxl.ui.infrastructure.audio.LocalSoundManager
import local.paxl.ui.presentation.components.GameScreenScaffold
import local.paxl.ui.presentation.components.NeonButton
import local.paxl.ui.presentation.components.NeonText
import local.paxl.ui.presentation.components.NeonTitle
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonBlue
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.NeonGreen
import local.paxl.ui.presentation.theme.NeonPink
import local.paxl.ui.presentation.theme.NeonYellow

@Composable
fun HomeScreen(
    coins: Int,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShopClick: () -> Unit,
    onDailyChallengeClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    HomeScreenContent(
        coins = coins,
        onPlayClick = onPlayClick,
        onSettingsClick = onSettingsClick,
        onShopClick = onShopClick,
        onDailyChallengeClick = onDailyChallengeClick,
        onLeaderboardClick = onLeaderboardClick,
        onHelpClick = onHelpClick
    )
}

@Composable
fun HomeScreenContent(
    coins: Int,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShopClick: () -> Unit,
    onDailyChallengeClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val soundManager = LocalSoundManager.current

    GameScreenScaffold {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .zIndex(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = "Tokens",
                    tint = NeonYellow,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(12.dp))

                NeonText(
                    text = "$coins",
                    color = Color.White,
                    fontSize = 24,
                )
            }

            Spacer(Modifier.width(24.dp))

            IconButton(
                onClick = {
                    soundManager.playClick()
                    onShopClick()
                }, modifier = Modifier.size(32.dp)
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonTitle(
                "PAXL",
                color = NeonCyan,
                fontSize = 48,
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NeonButton(
                    text = "PLAY GAME", color = NeonGreen, onClick = {
                        soundManager.playClick()
                        onPlayClick()
                    }, icon = Icons.Default.Gamepad
                )

                NeonButton(
                    text = "DAILY CHALLENGE", color = NeonYellow,
                    onClick = {
                        soundManager.playClick()
                        onDailyChallengeClick()
                    },
                    icon = Icons.Default.CalendarToday,
                )

                NeonButton(
                    text = "LEADERBOARD",
                    color = NeonBlue,
                    onClick = {
                        soundManager.playClick()
                        onLeaderboardClick()
                    },
                    icon = Icons.Default.EmojiEvents,
                )

                NeonButton(
                    text = "HELP",
                    color = NeonCyan,
                    onClick = {
                        soundManager.playClick()
                        onHelpClick()
                    },
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                )

                NeonButton(
                    text = "SETTINGS",
                    color = NeonPink,
                    onClick = {
                        soundManager.playClick()
                        onSettingsClick()
                    },
                    icon = Icons.Default.Settings,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GameTheme {
        HomeScreenContent(
            coins = 100,
            onPlayClick = {},
            onSettingsClick = {},
            onShopClick = {},
            onDailyChallengeClick = {},
            onLeaderboardClick = {},
            onHelpClick = {})
    }
}
