package local.paxl.ui.presentation.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.model.GridState
import local.paxl.engine.domain.service.DropSnapping
import local.paxl.shop.domain.service.PowerCosts
import local.paxl.ui.infrastructure.audio.LocalSoundManager
import local.paxl.ui.presentation.components.BlockItem
import local.paxl.ui.presentation.components.DragLiftDp
import local.paxl.ui.presentation.components.DragLogTag
import local.paxl.ui.presentation.components.DraggedBlockOverlay
import local.paxl.ui.presentation.components.GameGrid
import local.paxl.ui.presentation.components.NeonText
import local.paxl.ui.presentation.components.RetroFont
import local.paxl.ui.presentation.screens.gameplay.GameBoardMetrics
import local.paxl.ui.presentation.screens.gameplay.GameDragMapper
import local.paxl.ui.presentation.screens.gameplay.GameDragState
import local.paxl.ui.presentation.screens.gameplay.rememberElapsedSeconds
import local.paxl.ui.presentation.screens.gameplay.rememberGameBoardMetrics
import local.paxl.ui.presentation.screens.gameplay.rememberGameDragState
import local.paxl.ui.presentation.screens.gameplay.tetrisPreviewBlocks
import local.paxl.ui.presentation.theme.BackgroundDark
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonCyan
import local.paxl.ui.presentation.theme.NeonGreen
import local.paxl.ui.presentation.theme.NeonYellow
import local.paxl.ui.presentation.util.formatElapsedTime
import local.paxl.ui.presentation.viewmodel.GameViewModel

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
        onDropAt = { block, cell ->
            val placed = viewModel.onDropAtPointer(block, cell)
            if (placed) soundManager.playPlace()
            placed
        })
}

/**
 * Thin rendering layer: layout comes from [rememberGameBoardMetrics], drag
 * session from [rememberGameDragState], pointer math from [GameDragMapper],
 * and placement rules from the engine ([DropSnapping]). No business math
 * lives in this body.
 */
@Composable
fun GamePlayScreenContent(
    gameState: GameState,
    coins: Int,
    onPauseClick: () -> Unit,
    onGameOver: () -> Unit,
    onShopClick: () -> Unit,
    onReshuffle: () -> Unit,
    onUndo: () -> Unit,
    onDropAt: (Block, Coordinate) -> Boolean
) {
    val soundManager = LocalSoundManager.current
    val drag = rememberGameDragState()
    val density = LocalDensity.current

    // Back press mid-drag cancels the drag instead of leaving the screen.
    // Skipped in @Preview (no OnBackPressedDispatcherOwner there).
    if (!LocalInspectionMode.current) {
        BackHandler(enabled = drag.draggedBlock != null) {
            drag.cancel()
        }
    }

    val liftPx = with(density) { DragLiftDp.toPx() }
    val elapsedSeconds = rememberElapsedSeconds(gameState.sessionStartMs)

    val boardCells = gameState.grid.size
    val anchorOffset = drag.draggedOffset?.let { it.copy(y = it.y - liftPx) }
    val anchorCell = if (drag.draggedBlock != null && anchorOffset != null) {
        GameDragMapper.pointerCellFor(anchorOffset, drag.gridOffset, drag.gridCellSizePx)
    } else {
        null
    }
    val dragging = drag.draggedBlock
    // One engine call per frame drives ghost, blast highlight and drop
    // alike (see DropSnapping.previewDrop).
    val preview = if (dragging != null && anchorCell != null &&
        GameDragMapper.isInDropZone(anchorCell, boardCells)
    ) {
        DropSnapping.previewDrop(dragging, anchorCell, gameState.grid)
    } else {
        null
    }
    val dropCell = preview?.origin

    // Per-frame ghost tracking: what the board shows for the current finger.
    if (dragging != null) {
        Log.d(
            DragLogTag,
            "ghost block=${dragging.id} shape=${dragging.shape} anchorPx=$anchorOffset " +
                "anchorCell=$anchorCell gridPx=${drag.gridOffset}/${drag.gridCellSizePx} " +
                "inZone=${anchorCell?.let { GameDragMapper.isInDropZone(it, boardCells) }} " +
                "origin=${preview?.origin} ghostCells=${preview?.cells?.size ?: 0}"
        )
    }

    // THE drop function (finger release): old-style handoff — map the fresh
    // release finger to a pointer cell here (gesture state only, no board
    // data), then hand block + cell to the ViewModel, which resolves the
    // ghost AND places it on the same live grid object. Never resolves
    // against the composable-captured grid: it can lag the live board and
    // produce a "fit" the live grid then rejects (silent cancel). Self-checks:
    // 1. Cancelled gestures never place.
    // 2. Unmeasured grid, out-of-zone release → cancel.
    fun placeAt(fingerRootAtRelease: Offset) {
        // Self-check 1: back-press cancelled mid-drag.
        if (drag.dragCancelled) {
            Log.d(DragLogTag, "drop CANCEL reason=drag-cancelled")
            return
        }
        val block = drag.draggedBlock
        if (block == null) {
            Log.d(DragLogTag, "drop CANCEL reason=no-dragged-block")
            return
        }
        val releaseAnchor = fingerRootAtRelease.copy(y = fingerRootAtRelease.y - liftPx)
        // Self-check 2: grid must be measured.
        val releaseCell = GameDragMapper.pointerCellFor(
            releaseAnchor, drag.gridOffset, drag.gridCellSizePx
        )
        if (releaseCell == null) {
            Log.d(
                DragLogTag,
                "drop CANCEL block=${block.id} reason=grid-unmeasured " +
                    "releaseFinger=$fingerRootAtRelease gridPx=${drag.gridOffset}/${drag.gridCellSizePx}"
            )
            return
        }
        // Self-check 3: release must be inside the drop zone.
        if (!GameDragMapper.isInDropZone(releaseCell, boardCells)) {
            Log.d(
                DragLogTag,
                "drop CANCEL block=${block.id} reason=outside-drop-zone releaseCell=$releaseCell"
            )
            return
        }
        Log.d(
            DragLogTag,
            "drop block=${block.id} shape=${block.shape} releaseFinger=$fingerRootAtRelease " +
                "releaseCell=$releaseCell"
        )
        val placed = onDropAt(block, releaseCell)
        Log.d(DragLogTag, "drop block=${block.id} releaseCell=$releaseCell placed=$placed")
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
            // Measured budget (post-Scaffold-padding): metrics derives the
            // width-based board plus the chrome scale that provably fits.
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val metrics = rememberGameBoardMetrics(maxWidth, maxHeight)
                val previewCellSizePx =
                    with(density) { metrics.previewCellSize.toPx() }
                val dragCellSizePx =
                    with(density) { metrics.dragCellSize.toPx() }
                GamePlayLayout(
                    gameState = gameState,
                    coins = coins,
                    metrics = metrics,
                    drag = drag,
                    previewCellSizePx = previewCellSizePx,
                    elapsedSeconds = elapsedSeconds,
                    anchorOffset = anchorOffset,
                    dropCell = dropCell,
                    wouldClear = preview?.wouldClear.orEmpty(),
                    onPauseClick = onPauseClick,
                    onShopClick = onShopClick,
                    onReshuffle = onReshuffle,
                    onUndo = onUndo,
                    onPlaceAt = ::placeAt
                )

                // Floating overlay — renders the dragged block above ALL screen
                // content, lifted above the finger so the piece stays visible.
                // The anchor is in root coordinates; the overlay fills this
                // padded content box, so subtract the box origin to land the
                // piece exactly where the ghost/drop math says it is.
                // (A layout-only Box never consumes touches.)
                var overlayOrigin by remember { mutableStateOf(Offset.Zero) }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { overlayOrigin = it.positionInRoot() }
                ) {
                    if (drag.draggedBlock != null && anchorOffset != null) {
                        DraggedBlockOverlay(
                            block = drag.draggedBlock!!,
                            offset = anchorOffset - overlayOrigin,
                            cellSize = dragCellSizePx
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Phone 411dp", device = Devices.PHONE, showBackground = true)
@Preview(name = "Foldable 673dp", device = Devices.FOLDABLE, showBackground = true)
@Preview(name = "Tablet 800dp", device = Devices.TABLET, showBackground = true)
@Preview(
    name = "Wide 600x1000",
    device = "spec:width=600dp,height=1000dp,dpi=420",
    showBackground = true
)
@Composable
fun GamePlayScreenPreview() {
    GameTheme {
        val previewBlocks = tetrisPreviewBlocks().take(3)
        GamePlayScreenContent(
            gameState = GameState(
                grid = GridState(),
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
            onDropAt = { _, _ -> false })
    }
}

/**
 * The full gameplay column. Every row has a FIXED height and every text an
 * explicit lineHeight (see [GameBoardMetrics]), so the chrome total is exact
 * and the width-based board can never be pushed into its neighbors — on wide
 * screens only the chrome compacts, the board keeps its size.
 */
@Composable
private fun GamePlayLayout(
    gameState: GameState,
    coins: Int,
    metrics: GameBoardMetrics,
    drag: GameDragState,
    previewCellSizePx: Float,
    elapsedSeconds: Long,
    anchorOffset: Offset?,
    dropCell: Coordinate?,
    wouldClear: Set<Coordinate>,
    onPauseClick: () -> Unit,
    onShopClick: () -> Unit,
    onReshuffle: () -> Unit,
    onUndo: () -> Unit,
    onPlaceAt: (Offset) -> Unit
) {
    val soundManager = LocalSoundManager.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .width(metrics.boardSize)
                .height(metrics.headerRowHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "LEVEL ${gameState.currentLevel}",
                    color = NeonCyan,
                    fontFamily = RetroFont,
                    fontSize = metrics.levelFontSize,
                    lineHeight = metrics.levelLineHeight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "SCORE: ${gameState.score}",
                    color = NeonGreen,
                    fontFamily = RetroFont,
                    fontSize = metrics.scoreFontSize,
                    lineHeight = metrics.scoreLineHeight,
                    fontWeight = FontWeight.ExtraBold
                )
                // Fixed-height combo slot: showing/hiding COMBO must not
                // change the Column height (which previously resized
                // the weight-driven board on combo clears).
                Box(modifier = Modifier.height(metrics.comboSlotHeight)) {
                    if (gameState.combo.value >= 2) {
                        Text(
                            "COMBO x${gameState.combo.value}",
                            color = NeonYellow,
                            fontFamily = RetroFont,
                            fontSize = metrics.comboFontSize,
                            lineHeight = metrics.comboLineHeight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .width(metrics.boardSize)
                .height(metrics.timerRowHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                formatElapsedTime(elapsedSeconds),
                color = NeonCyan,
                fontFamily = RetroFont,
                fontSize = metrics.timerFontSize,
                lineHeight = metrics.timerLineHeight,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = "Tokens",
                    tint = NeonYellow,
                    modifier = Modifier.size(metrics.coinIconSize)
                )
                Spacer(Modifier.width(4.dp * metrics.chromeScale))
                Text(
                    "$coins",
                    color = NeonYellow,
                    fontFamily = RetroFont,
                    fontSize = metrics.coinFontSize,
                    lineHeight = metrics.coinLineHeight,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Fixed height (2 lines @ goalLineHeight) so single-line "GOAL SKIPPED"
        // never changes the Column height and shifts the board.
        Box(
            modifier = Modifier
                .width(metrics.boardSize)
                .height(metrics.goalBoxHeight),
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
                fontSize = metrics.goalFontSize,
                lineHeight = metrics.goalLineHeight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(metrics.sectionGap))

        // Game Grid Area: exact width-based size (legacy logic). weight(1f)
        // only centers it in the leftover space; it never sizes it.
        Box(
            modifier = Modifier
                .weight(1f)
                .width(metrics.boardSize),
            contentAlignment = Alignment.Center
        ) {
            GameGrid(
                gridState = gameState.grid,
                draggedBlock = drag.draggedBlock,
                anchorOffset = anchorOffset,
                dropCell = dropCell,
                wouldClear = wouldClear,
                isClearing = gameState.isClearing,
                clearingCells = gameState.clearingCells,
                gridOffset = drag.gridOffset,
                onGridMeasured = drag::onGridMeasured,
                modifier = Modifier.size(metrics.boardSize))
        }

        // Power Actions: fixed height, same width as the board so buttons stay
        // aligned to its edges.
        Row(
            modifier = Modifier
                .width(metrics.boardSize)
                .height(metrics.powerRowHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PowerActionButton(
                icon = { Icon(Icons.Default.Pause, "Pause", tint = Color.White) },
                label = "PAUSE",
                enabled = true,
                buttonSize = metrics.powerButtonSize,
                labelFontSize = metrics.powerLabelSize,
                labelLineHeight = metrics.powerLabelLineHeight,
                contentGap = metrics.powerContentGap,
                onClick = {
                    soundManager.playClick()
                    onPauseClick()
                })
            PowerActionButton(
                icon = { Icon(Icons.Default.Refresh, "Shuffle", tint = Color.White) },
                label = "SHUFFLE",
                enabled = coins >= PowerCosts.RESHUFFLE_COST && !gameState.isGameOver,
                buttonSize = metrics.powerButtonSize,
                labelFontSize = metrics.powerLabelSize,
                labelLineHeight = metrics.powerLabelLineHeight,
                contentGap = metrics.powerContentGap,
                onClick = {
                    soundManager.playClick()
                    onReshuffle()
                })
            PowerActionButton(
                icon = { Icon(Icons.AutoMirrored.Filled.Undo, "Undo", tint = Color.White) },
                label = "UNDO",
                enabled = coins >= PowerCosts.UNDO_COST && !gameState.isGameOver,
                buttonSize = metrics.powerButtonSize,
                labelFontSize = metrics.powerLabelSize,
                labelLineHeight = metrics.powerLabelLineHeight,
                contentGap = metrics.powerContentGap,
                onClick = {
                    soundManager.playClick()
                    onUndo()
                })
        }

        // Fixed-height slot: showing/hiding this must not resize the board.
        Box(
            modifier = Modifier
                .width(metrics.boardSize)
                .height(metrics.noticeSlotHeight),
            contentAlignment = Alignment.Center
        ) {
            if (coins < PowerCosts.CHEAPEST_COST && !gameState.isGameOver) {
                NeonText(
                    text = "OUT OF COINS — TAP FOR SHOP",
                    color = NeonYellow,
                    fontSize = metrics.noticeFontSize,
                    modifier = Modifier
                        .clickable { onShopClick() }
                )
            }
        }

        // Cancel hint slot: fixed height so showing it never shifts the
        // grid mid-drag. Visible only while a piece is being dragged.
        Box(
            modifier = Modifier
                .width(metrics.boardSize)
                .height(metrics.noticeSlotHeight),
            contentAlignment = Alignment.Center
        ) {
            if (drag.draggedBlock != null) {
                NeonText(
                    text = "RELEASE OVER THE TRAY TO CANCEL",
                    color = NeonYellow,
                    fontSize = metrics.noticeFontSize
                )
            }
        }

        // Available blocks (also the cancel zone: releasing here
        // returns the piece without placing). Width matches board.
        val trayModifier = Modifier
            .width(metrics.boardSize)
            .height(metrics.trayHeight)
            .padding(bottom = metrics.trayBottomPadding)
        Surface(
            modifier = if (drag.draggedBlock != null) {
                trayModifier.border(
                    1.dp,
                    NeonYellow.copy(alpha = 0.6f),
                    RoundedCornerShape(8.dp)
                )
            } else {
                trayModifier
            },
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                gameState.availableBlocks.forEach { block ->
                    Box(
                        modifier = Modifier.size(metrics.trayItemSize),
                        contentAlignment = Alignment.Center
                    ) {
                        BlockItem(
                            block = block,
                            gridOffset = drag.gridOffset,
                            cellSize = previewCellSizePx,
                            gridCellSize = drag.gridCellSizePx,
                            isBeingDragged = drag.draggedBlock?.id == block.id,
                            onDragging = { b, offset ->
                                if (b == null) {
                                    // Finger lifted: end of gesture, clear cancel flag.
                                    drag.endGesture()
                                } else if (!drag.dragCancelled) {
                                    drag.draggedBlock = b
                                    drag.draggedOffset = offset
                                }
                            },
                            onPlace = { rawDropPoint ->
                                onPlaceAt(rawDropPoint)
                            })
                    }
                }
            }
        }
    }
}

@Composable
private fun PowerActionButton(
    icon: @Composable () -> Unit,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    buttonSize: Dp = 56.dp,
    labelFontSize: TextUnit = 12.sp,
    labelLineHeight: TextUnit = 16.sp,
    contentGap: Dp = 4.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        // Plain Box (not IconButton): IconButton enforces a 48.dp minimum touch
        // target, which would break the exact fixed-height row math when the
        // chrome compacts on short screens.
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(Color(0xFF2D2D3A))
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(Modifier.height(contentGap))
        Text(
            text = label,
            color = if (enabled) NeonYellow else Color.Gray,
            fontSize = labelFontSize,
            lineHeight = labelLineHeight,
            fontWeight = FontWeight.Bold,
            fontFamily = RetroFont
        )
    }
}
