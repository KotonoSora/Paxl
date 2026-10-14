package local.paxl.ui.presentation.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.paxl.engine.domain.model.RunEndCause
import local.paxl.engine.domain.model.RunRecord
import local.paxl.ui.presentation.components.GameBackHeader
import local.paxl.ui.presentation.components.GameScreenScaffold
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonBlue
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.SurfaceDark
import local.paxl.ui.presentation.util.formatElapsedTime
import local.paxl.ui.presentation.viewmodel.LeaderboardViewModel

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel, onBack: () -> Unit) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    LeaderboardScreenContent(leaderboard = leaderboard, onBack = onBack)
}

@Composable
fun LeaderboardScreenContent(
    leaderboard: List<RunRecord>, onBack: () -> Unit
) {

    GameScreenScaffold {
        GameBackHeader(
            title = "TOP SCORES", titleColor = NeonBlue, titleFontSize = 24, onBack = onBack
        )

        Spacer(Modifier.height(24.dp))

        LeaderboardHeaderRow()
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            itemsIndexed(leaderboard) { index, entry ->
                LeaderboardEntryRow(
                    rank = index + 1,
                    timeCount = formatElapsedTime(entry.durationMs / 1000L),
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
                            text = "No records yet", color = Color.White.copy(alpha = 0.8f)
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
            leaderboard = PreviewData.fakeRuns, onBack = {})
    }
}

@Preview(showBackground = true, name = "Empty Leaderboard")
@Composable
fun LeaderboardScreenEmptyPreview() {
    GameTheme {
        LeaderboardScreenContent(leaderboard = emptyList(), onBack = {})
    }
}

@Composable
private fun LeaderboardHeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = "SCORE",
            color = NeonBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Right,
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
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = score.toString(),
            color = NeonCyan,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Right,
        )
    }
}
