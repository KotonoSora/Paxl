package com.jn.paxl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.paxl.model.LeaderboardEntry
import com.jn.paxl.ui.components.GameBackHeader
import com.jn.paxl.ui.components.GameScreenScaffold
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.ui.theme.NeonBlue
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.SurfaceDark
import com.jn.paxl.viewmodel.GameViewModel

@Composable
fun LeaderboardScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    LeaderboardScreenContent(leaderboard = leaderboard, onBack = onBack)
}

@Composable
private fun LeaderboardScreenContent(
    leaderboard: List<LeaderboardEntry>,
    onBack: () -> Unit
) {

    GameScreenScaffold {
        GameBackHeader(
            title = "TOP SCORES",
            titleColor = NeonBlue,
            titleFontSize = 36,
            onBack = onBack
        )

        Spacer(Modifier.height(32.dp))

        LeaderboardHeaderRow()
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(leaderboard) { index, entry ->
                LeaderboardEntryRow(
                    rank = index + 1,
                    timeCount = formatElapsedTime(entry.durationSeconds),
                    score = entry.score
                )
            }

            if (leaderboard.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark, RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No records yet",
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    GameTheme {
        LeaderboardScreenContent(
            leaderboard = listOf(
                LeaderboardEntry(score = 15000, durationSeconds = 180, recordedAtEpochMs = 3),
                LeaderboardEntry(score = 15000, durationSeconds = 200, recordedAtEpochMs = 2),
                LeaderboardEntry(score = 12000, durationSeconds = 170, recordedAtEpochMs = 1)
            ),
            onBack = {}
        )
    }
}

@Composable
private fun LeaderboardHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "RANK",
            color = NeonBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "TIME",
            color = NeonBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "SCORE",
            color = NeonBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun LeaderboardEntryRow(rank: Int, timeCount: String, score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = rank.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = timeCount,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = score.toString(),
            color = NeonCyan,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(2f)
        )
    }
}

private fun formatElapsedTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

