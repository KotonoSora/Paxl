package com.jn.paxl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.paxl.ui.components.GameBackHeader
import com.jn.paxl.ui.components.GameScreenScaffold
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.ui.theme.NeonBlue
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.SurfaceDark

@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
    val mockLeaderboard = listOf(
        "Player One" to 15000,
        "MasterBlaster" to 12400,
        "NeonKing" to 11000,
        "PaxlPro" to 9500,
        "StarDust" to 8200
    )

    GameScreenScaffold {
        GameBackHeader(
            title = "TOP SCORES",
            titleColor = NeonBlue,
            titleFontSize = 36,
            onBack = onBack
        )

        Spacer(Modifier.height(32.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(mockLeaderboard) { index, entry ->
                LeaderboardEntryRow(rank = index + 1, name = entry.first, score = entry.second)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    GameTheme {
        LeaderboardScreen(onBack = {})
    }
}

@Composable
private fun LeaderboardEntryRow(rank: Int, name: String, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "$rank. $name",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text("$score", color = NeonCyan, fontWeight = FontWeight.Bold)
    }
}
