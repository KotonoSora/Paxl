package local.paxl.ui.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.paxl.ui.presentation.components.GameBackHeader
import local.paxl.ui.presentation.components.GameScreenScaffold
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.NeonPink
import local.paxl.ui.presentation.theme.SurfaceDark

@Composable
fun LevelSelectScreen(
    onLevelSelected: (Int) -> Unit, onBack: () -> Unit
) {
    GameScreenScaffold {
        GameBackHeader(
            title = "LEVELS", titleColor = NeonPink, titleFontSize = 24, onBack = onBack
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(20) { index ->
                val level = index + 1
                LevelTile(level = level, onClick = { onLevelSelected(level) })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LevelSelectScreenPreview() {
    GameTheme {
        LevelSelectScreen(onLevelSelected = {}, onBack = {})
    }
}

@Composable
private fun LevelTile(level: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, NeonCyan)
    ) {
        Text(
            "$level",
            color = NeonCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            softWrap = false,
        )
    }
}
