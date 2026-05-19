package com.jn.paxl.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.model.GameUiState
import com.jn.paxl.model.GridState
import com.jn.paxl.ui.LocalSoundManager
import com.jn.paxl.ui.components.BlockItem
import com.jn.paxl.ui.components.GameGrid
import com.jn.paxl.ui.components.RetroFont
import com.jn.paxl.ui.theme.BackgroundDark
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.NeonGreen
import com.jn.paxl.ui.theme.NeonYellow
import com.jn.paxl.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    onPauseClick: () -> Unit,
    onGameOver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    GamePlayScreenContent(
        uiState = uiState,
        onPauseClick = onPauseClick,
        onGameOver = onGameOver,
        onReshuffle = { viewModel.reshuffleBlocks() },
        onUndo = { viewModel.undoMove() },
        onBlockPlaced = { block, pos -> viewModel.onBlockPlaced(block, pos) }
    )
}

@Composable
fun GamePlayScreenContent(
    uiState: GameUiState,
    onPauseClick: () -> Unit,
    onGameOver: () -> Unit,
    onReshuffle: () -> Unit,
    onUndo: () -> Unit,
    onBlockPlaced: (Block, Coordinate) -> Unit
) {
    val soundManager = LocalSoundManager.current
    var draggedBlock by remember { mutableStateOf<Block?>(null) }
    var draggedOffset by remember { mutableStateOf<Offset?>(null) }
    var gridOffset by remember { mutableStateOf(Offset.Zero) }
    var gridCellSizePx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    val previewCellSizePx = with(density) { 24.dp.toPx() }
    val elapsedSeconds by produceState(initialValue = 0L, key1 = uiState.sessionStartMs) {
        while (true) {
            value =
                ((System.currentTimeMillis() - uiState.sessionStartMs) / 1000L).coerceAtLeast(0L)
            delay(1000)
        }
    }


    LaunchedEffect(uiState.isGameOver) {
        if (uiState.isGameOver) onGameOver()
    }

    LaunchedEffect(uiState.isClearing) {
        if (uiState.isClearing) {
            soundManager?.playVanish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "LEVEL ${uiState.currentLevel}",
                        color = NeonCyan,
                        fontFamily = RetroFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "SCORE: ${uiState.score}",
                        color = NeonGreen,
                        fontFamily = RetroFont,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = "Tokens",
                        tint = NeonYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${uiState.tokens}",
                        color = NeonYellow,
                        fontFamily = RetroFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Timer display
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    formatElapsedTime(elapsedSeconds),
                    color = NeonCyan,
                    fontFamily = RetroFont,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.isWinConditionSkipped) {
                        "${uiState.playMode.name} GOAL SKIPPED"
                    } else {
                        "${uiState.playMode.name} GOAL: ${uiState.targetScore}  WIN +${uiState.winTokenReward}"
                    },
                    color = NeonYellow,
                    fontFamily = RetroFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            // Game Grid Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                GameGrid(
                    gridState = uiState.grid,
                    draggedBlock = draggedBlock,
                    draggedOffset = draggedOffset,
                    isClearing = uiState.isClearing,
                    clearingCells = uiState.clearingCells,
                    gridOffset = gridOffset,
                    onGridMeasured = { offset, measuredCellSizePx ->
                        if (gridOffset != offset) gridOffset = offset
                        if (abs(gridCellSizePx - measuredCellSizePx) > 0.5f) {
                            gridCellSizePx = measuredCellSizePx
                        }
                    }
                )
            }

            // Power Actions: Pause, Refresh, Undo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PowerActionButton(
                    icon = { Icon(Icons.Default.Pause, "Pause", tint = Color.White) },
                    cost = "PAUSE",
                    onClick = {
                        soundManager?.playClick()
                        onPauseClick()
                    }
                )
                PowerActionButton(
                    icon = { Icon(Icons.Default.Refresh, "Shuffle", tint = Color.White) },
                    cost = "25",
                    onClick = {
                        soundManager?.playClick()
                        onReshuffle()
                    }
                )
                PowerActionButton(
                    icon = { Icon(Icons.AutoMirrored.Filled.Undo, "Undo", tint = Color.White) },
                    cost = "10",
                    onClick = {
                        soundManager?.playClick()
                        onUndo()
                    }
                )
            }

            // Available blocks
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(bottom = 12.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiState.availableBlocks.forEach { block ->
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(Color(0xFF1A1A2E), CircleShape)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BlockItem(
                                block = block,
                                gridOffset = gridOffset,
                                cellSize = previewCellSizePx,
                                gridCellSize = gridCellSizePx,
                                onDragging = { b, offset ->
                                    draggedBlock = b
                                    draggedOffset = offset
                                },
                                onPlace = { pos ->
                                    soundManager?.playPlace()
                                    onBlockPlaced(block, pos)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamePlayScreenPreview() {
    GameTheme {
        GamePlayScreenContent(
            uiState = GameUiState(
                grid = GridState(size = 10),
                tokens = 100,
                score = 500,
                currentLevel = 1
            ),
            onPauseClick = {},
            onGameOver = {},
            onReshuffle = {},
            onUndo = {},
            onBlockPlaced = { _, _ -> }
        )
    }
}

@Composable
private fun PowerActionButton(
    icon: @Composable () -> Unit,
    cost: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFF2D2D3A), CircleShape)
        ) {
            icon()
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = cost,
            color = NeonYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = RetroFont
        )
    }
}

private fun formatElapsedTime(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
