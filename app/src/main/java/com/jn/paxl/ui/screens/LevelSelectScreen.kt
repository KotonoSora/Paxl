package com.jn.paxl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jn.paxl.ui.components.PaxlBackHeader
import com.jn.paxl.ui.components.PaxlScreenScaffold
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.NeonPink
import com.jn.paxl.ui.theme.SurfaceDark

import androidx.compose.ui.tooling.preview.Preview
import com.jn.paxl.ui.theme.PaxlTheme

@Composable
fun LevelSelectScreen(
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    PaxlScreenScaffold {
        PaxlBackHeader(
            title = "LEVELS",
            titleColor = NeonPink,
            titleFontSize = 40,
            onBack = onBack
        )

        Spacer(Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
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
    PaxlTheme {
        LevelSelectScreen(onLevelSelected = {}, onBack = {})
    }
}

@Composable
private fun LevelTile(level: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, NeonCyan)
    ) {
        Text("$level", color = NeonCyan, fontWeight = FontWeight.Bold)
    }
}
