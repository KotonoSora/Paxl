package com.kotonosora.paxl.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotonosora.paxl.ui.components.PaxlBackHeader
import com.kotonosora.paxl.ui.components.PaxlScreenScaffold
import com.kotonosora.paxl.ui.theme.NeonCyan
import com.kotonosora.paxl.ui.theme.NeonGreen
import com.kotonosora.paxl.ui.theme.NeonPink
import com.kotonosora.paxl.ui.theme.NeonYellow

@Composable
fun HelpScreen(onBack: () -> Unit) {
    PaxlScreenScaffold(scrollable = true) {
        PaxlBackHeader(
            title = "HELP",
            titleColor = NeonCyan,
            titleFontSize = 40,
            onBack = onBack
        )

        Spacer(Modifier.height(32.dp))

        HelpSection(
            title = "HOW TO PLAY",
            content = "Drag blocks from the bottom onto the 10x10 grid. Place blocks to create full lines vertically or horizontally.",
            color = NeonGreen
        )

        HelpSection(
            title = "SCORING",
            content = "You earn points for every block placed and bonus points for clearing lines. Clearing multiple lines at once gives a massive score boost!",
            color = NeonPink
        )

        HelpSection(
            title = "POWER-UPS",
            content = "Undo: Revert your last move for 10 coins.\nShuffle: Get a new set of blocks for 25 coins.",
            color = NeonYellow
        )

        HelpSection(
            title = "GAME OVER",
            content = "The game ends when there's no more space to place any of the available blocks on the grid.",
            color = NeonCyan
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun HelpSection(title: String, content: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = title,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = content,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}
