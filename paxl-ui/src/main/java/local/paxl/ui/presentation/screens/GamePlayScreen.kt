package local.paxl.ui.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.BlockShapes
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.model.GridState
import local.paxl.shop.domain.service.PowerCosts
import local.paxl.ui.infrastructure.audio.LocalSoundManager
import local.paxl.ui.presentation.components.BlockItem
import local.paxl.ui.presentation.components.DraggedBlockOverlay
import local.paxl.ui.presentation.components.GameGrid
import local.paxl.ui.presentation.components.NeonText
import local.paxl.ui.presentation.components.RetroFont
import local.paxl.ui.presentation.theme.BackgroundDark
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.NeonGreen
import local.paxl.ui.presentation.theme.NeonYellow
import local.paxl.ui.presentation.util.formatElapsedTime
import local.paxl.ui.presentation.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun GamePlayScreen(
    viewModel: GameViewModel,
    coins: Int,
    onPauseClick: () -> Unit,
    onGameOver: () -> Unit,
    onShopClick: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val soundManager = LocalSoundManager.current

    GamePlayScreenContent(
        gameState = gameState,
        coins = coins,
        onPauseClick = onPauseClick,
        onGameOver = onGameOver,
        onShopClick = onShopClick,
        onReshuffle = { viewModel.reshuffleBlocks() },
        onUndo = { viewModel.undoMove() },
        onBlockPlaced = { block, pos ->
            if (viewModel.onBlockPlaced(block, pos)) {
                soundManager.playPlace()
            }
        })
}

@Composable
fun GamePlayScreenContent(
    gameState: GameState,
    coins: Int,
    onPauseClick: () -> Unit,
    onGameOver: () -> Unit,
    onShopClick: () -> Unit,
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
    val elapsedSeconds by produceState(initialValue = 0L, key1 = gameState.sessionStartMs) {
        while (true) {
            value =
                ((System.currentTimeMillis() - gameState.sessionStartMs) / 1000L).coerceAtLeast(0L)
            delay(1000)
        }
    }


    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) onGameOver()
    }

    LaunchedEffect(gameState.clearAnimationId) {
        if (gameState.isClearing && gameState.clearAnimationId > 0L) {
            soundManager.playVanish()
        }
    }

    Scaffold(containerColor = BackgroundDark) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundDark)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                            "LEVEL ${gameState.currentLevel}",
                            color = NeonCyan,
                            fontFamily = RetroFont,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "SCORE: ${gameState.score}",
                            color = NeonGreen,
                            fontFamily = RetroFont,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formatElapsedTime(elapsedSeconds),
                        color = NeonCyan,
                        fontFamily = RetroFont,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = "Tokens",
                            tint = NeonYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$coins",
                            color = NeonYellow,
                            fontFamily = RetroFont,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (gameState.isWinConditionSkipped) {
                            "${gameState.playMode.name} GOAL SKIPPED"
                        } else {
                            "${gameState.playMode.name} GOAL: ${gameState.targetScore}  \nWIN +${gameState.winReward}"
                        },
                        color = NeonYellow,
                        fontFamily = RetroFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
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
                        gridState = gameState.grid,
                        draggedBlock = draggedBlock,
                        draggedOffset = draggedOffset,
                        isClearing = gameState.isClearing,
                        clearingCells = gameState.clearingCells,
                        gridOffset = gridOffset,
                        onGridMeasured = { offset, measuredCellSizePx ->
                            if (gridOffset != offset) gridOffset = offset
                            if (abs(gridCellSizePx - measuredCellSizePx) > 0.5f) {
                                gridCellSizePx = measuredCellSizePx
                            }
                        })
                }

                // Power Actions: Pause, Shuffle, Undo (coin-gated, no cost on the button)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PowerActionButton(
                        icon = { Icon(Icons.Default.Pause, "Pause", tint = Color.White) },
                        label = "PAUSE",
                        enabled = true,
                        onClick = {
                            soundManager.playClick()
                            onPauseClick()
                        })
                    PowerActionButton(
                        icon = { Icon(Icons.Default.Refresh, "Shuffle", tint = Color.White) },
                        label = "SHUFFLE",
                        enabled = coins >= PowerCosts.RESHUFFLE_COST && !gameState.isGameOver,
                        onClick = {
                            soundManager.playClick()
                            onReshuffle()
                        })
                    PowerActionButton(
                        icon = { Icon(Icons.AutoMirrored.Filled.Undo, "Undo", tint = Color.White) },
                        label = "UNDO",
                        enabled = coins >= PowerCosts.UNDO_COST && !gameState.isGameOver,
                        onClick = {
                            soundManager.playClick()
                            onUndo()
                        })
                }

                if (coins < PowerCosts.CHEAPEST_COST && !gameState.isGameOver) {
                    NeonText(
                        text = "OUT OF COINS — TAP FOR SHOP",
                        color = NeonYellow,
                        fontSize = 12,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { onShopClick() }
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
                        gameState.availableBlocks.forEach { block ->
                            Box(
                                modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center
                            ) {
                                BlockItem(
                                    block = block,
                                    gridOffset = gridOffset,
                                    cellSize = previewCellSizePx,
                                    gridCellSize = gridCellSizePx,
                                    isBeingDragged = draggedBlock?.id == block.id,
                                    onDragging = { b, offset ->
                                        draggedBlock = b
                                        draggedOffset = offset
                                    },
                                    onPlace = { pos ->
                                        onBlockPlaced(block, pos)
                                    })
                            }
                        }
                    }
                }
            }

            // Floating overlay — renders the dragged block above ALL screen content
            if (draggedBlock != null && draggedOffset != null) {
                DraggedBlockOverlay(
                    block = draggedBlock!!, offset = draggedOffset!!, cellSize = previewCellSizePx
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamePlayScreenPreview() {
    GameTheme {
        val previewBlocks = BlockShapes.getPreviewBlocks().take(3)
        GamePlayScreenContent(
            gameState = GameState(
                grid = GridState(size = 10),
                availableBlocks = previewBlocks,
                score = 500,
                currentLevel = 1
            ),
            coins = 100,
            onPauseClick = {},
            onGameOver = {},
            onShopClick = {},
            onReshuffle = {},
            onUndo = {},
            onBlockPlaced = { _, _ -> })
    }
}

@Composable
private fun PowerActionButton(
    icon: @Composable () -> Unit,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFF2D2D3A), CircleShape)
        ) {
            icon()
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = if (enabled) NeonYellow else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = RetroFont
        )
    }
}
