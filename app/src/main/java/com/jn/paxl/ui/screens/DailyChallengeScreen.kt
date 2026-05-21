package com.jn.paxl.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jn.paxl.model.PlayMode
import com.jn.paxl.ui.components.GameBackHeader
import com.jn.paxl.ui.components.GameScreenScaffold
import com.jn.paxl.ui.components.NeonButton
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.ui.theme.NeonGreen
import com.jn.paxl.ui.theme.NeonYellow
import com.jn.paxl.ui.theme.SurfaceDark
import com.jn.paxl.viewmodel.GameViewModel

@Composable
fun DailyChallengeScreen(
    viewModel: GameViewModel = viewModel(), onBack: () -> Unit, onStartChallenge: () -> Unit = {}
) {
    DailyChallengeScreenContent(
        onBack = onBack, onStartChallenge = {
            viewModel.startNewGame(mode = PlayMode.DAILY)
            onStartChallenge()
        })
}

@Composable
fun DailyChallengeScreenContent(
    onBack: () -> Unit, onStartChallenge: () -> Unit = {}
) {
    GameScreenScaffold {
        GameBackHeader(
            title = "DAILY", titleColor = NeonYellow, titleFontSize = 24, onBack = onBack
        )

        Spacer(Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, NeonYellow.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Today's Challenge",
                    color = NeonYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Score 5000 points in a single session to win 50 tokens bonus!",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        NeonButton(
            text = "START CHALLENGE", color = NeonGreen, onClick = onStartChallenge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyChallengeScreenPreview() {
    GameTheme {
        DailyChallengeScreenContent(onBack = {})
    }
}
