package local.paxl.ui.presentation.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.GridState
import local.paxl.ui.presentation.screens.gameplay.tetrisPreviewBlocks
import local.paxl.ui.presentation.theme.GameTheme
import local.paxl.ui.presentation.theme.NeonCyan
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

/** Engine ARGB longs render as Compose colors at this boundary. */
private fun Long.toDisplayColor(): Color = Color(this)

/** Logcat tag for drag/drop tracking (filter: `adb logcat -s PaxlDrag`). */
const val DragLogTag = "PaxlDrag"

/**
 * How far above the finger the dragged piece floats, so the piece and its
 * landing ghost stay visible while dragging. Screens subtract this from the
 * reported pointer and map/snaps from the lifted anchor for BOTH the ghost
 * and the drop.
 */
val DragLiftDp = 64.dp

@Composable
fun GameGrid(
    gridState: GridState,
    draggedBlock: Block?,
    /** Lifted piece anchor (finger minus [DragLiftDp]); null when not dragging. */
    anchorOffset: Offset?,
    /** Snapped landing cell from `DropSnapping.snapToPlacement`; null = no fit. */
    dropCell: Coordinate?,
    /** Rows/cols that would blast on drop (from `DropSnapping.previewDrop`). */
    wouldClear: Set<Coordinate> = emptySet(),
    isClearing: Boolean,
    clearingCells: Map<Coordinate, Long?>,
    gridOffset: Offset,
    onGridMeasured: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NeonGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, // Increased from 0.1f
        targetValue = 0.5f, // Increased from 0.3f
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ), label = "GlowAlpha"
    )
    val vanishProgress by animateFloatAsState(
        targetValue = if (isClearing) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "VanishProgress"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF130730))
            .onGloballyPositioned {
                val measuredCellSize = it.size.width.toFloat() / gridState.size
                onGridMeasured(it.positionInRoot(), measuredCellSize)
            }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSizePx = size.width / gridState.size

            // Draw grid lines with better visibility
            for (i in 0..gridState.size) {
                val pos = i * cellSizePx
                drawLine(
                    color = NeonCyan.copy(alpha = 0.35f),
                    start = Offset(0f, pos),
                    end = Offset(size.width, pos),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = NeonCyan.copy(alpha = 0.35f),
                    start = Offset(pos, 0f),
                    end = Offset(pos, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

            drawRoundRect(
                color = NeonCyan.copy(alpha = 0.6f),
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Draw placed blocks
            gridState.cells.forEach { (coord, colorArgb) ->
                if (colorArgb != null) {
                    val color = colorArgb.toDisplayColor()
                    val topLeft = Offset(coord.x * cellSizePx, coord.y * cellSizePx)
                    // Inner glow / background for block
                    drawRoundRect(
                        color = color.copy(alpha = 0.4f), // Increased from 0.3f
                        topLeft = topLeft,
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    // Main block body
                    drawRoundRect(
                        color = color,
                        topLeft = topLeft.plus(Offset(2.dp.toPx(), 2.dp.toPx())),
                        size = Size(cellSizePx - 4.dp.toPx(), cellSizePx - 4.dp.toPx()),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    // White inner border for better definition
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.4f),
                        topLeft = topLeft.plus(Offset(2.dp.toPx(), 2.dp.toPx())),
                        size = Size(cellSizePx - 4.dp.toPx(), cellSizePx - 4.dp.toPx()),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            if (isClearing) {
                clearingCells.forEach { (coord, colorArgb) ->
                    if (colorArgb != null) {
                        val color = colorArgb.toDisplayColor()
                        val topLeft = Offset(coord.x * cellSizePx, coord.y * cellSizePx)
                        val alpha = 1f - vanishProgress
                        val shrink = 0.45f * vanishProgress
                        val dynamicInset = (cellSizePx * shrink) / 2f
                        val baseInset = 2.dp.toPx()
                        val bodyTopLeft =
                            topLeft.plus(Offset(baseInset + dynamicInset, baseInset + dynamicInset))
                        val bodySize =
                            (cellSizePx - (2 * baseInset) - (2 * dynamicInset)).coerceAtLeast(0f)

                        drawRoundRect(
                            color = color.copy(alpha = 0.4f * alpha),
                            topLeft = topLeft,
                            size = Size(cellSizePx, cellSizePx),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )
                        if (bodySize > 0f) {
                            drawRoundRect(
                                color = color.copy(alpha = alpha),
                                topLeft = bodyTopLeft,
                                size = Size(bodySize, bodySize),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.4f * alpha),
                                topLeft = bodyTopLeft,
                                size = Size(bodySize, bodySize),
                                cornerRadius = CornerRadius(4.dp.toPx()),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }
                    }
                }
            }

            // Draw Blast Highlight: rows/cols that would clear on drop.
            // Drawn under the ghost so the landing piece stays on top.
            if (draggedBlock != null && dropCell != null && wouldClear.isNotEmpty()) {
                wouldClear.forEach { coord ->
                    val targetTopLeft = Offset(coord.x * cellSizePx, coord.y * cellSizePx)
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.18f),
                        topLeft = targetTopLeft,
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.7f),
                        topLeft = targetTopLeft,
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // Draw Ghost Block Preview (Landing Zone) at the snapped cell.
            // The snap already guarantees a fit, so the ghost always matches
            // the drop. When the anchor is over the grid but nothing fits,
            // tint the covered cells red instead.
            if (draggedBlock != null && dropCell != null) {
                val draggedColor = draggedBlock.colorArgb.toDisplayColor()
                draggedBlock.shape.forEach { offset ->
                    val targetTopLeft =
                        Offset(
                            (dropCell.x + offset.x) * cellSizePx,
                            (dropCell.y + offset.y) * cellSizePx
                        )
                    drawRoundRect(
                        color = draggedColor.copy(alpha = glowAlpha),
                        topLeft = targetTopLeft,
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    drawRoundRect(
                        color = draggedColor,
                        topLeft = targetTopLeft,
                        size = Size(cellSizePx, cellSizePx),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            } else if (draggedBlock != null && anchorOffset != null) {
                val relativeX = anchorOffset.x - gridOffset.x
                val relativeY = anchorOffset.y - gridOffset.y
                val gridX = floor(relativeX / cellSizePx).toInt()
                val gridY = floor(relativeY / cellSizePx).toInt()

                val isOverGrid = draggedBlock.shape.any { offset ->
                    val x = gridX + offset.x
                    val y = gridY + offset.y
                    x in 0 until gridState.size && y in 0 until gridState.size
                }

                if (isOverGrid) {
                    // Show conflict color when no snapped placement fits here.
                    val conflictColor = Color(0xFFFF3333)
                    draggedBlock.shape.forEach { offset ->
                        val x = gridX + offset.x
                        val y = gridY + offset.y
                        if (x in 0 until gridState.size && y in 0 until gridState.size) {
                            val targetTopLeft = Offset(x * cellSizePx, y * cellSizePx)
                            drawRoundRect(
                                color = conflictColor.copy(alpha = 0.35f),
                                topLeft = targetTopLeft,
                                size = Size(cellSizePx, cellSizePx),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                            drawRoundRect(
                                color = conflictColor,
                                topLeft = targetTopLeft,
                                size = Size(cellSizePx, cellSizePx),
                                cornerRadius = CornerRadius(4.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlockItem(
    block: Block,
    gridOffset: Offset,
    cellSize: Float,
    gridCellSize: Float,
    isBeingDragged: Boolean = false,
    onDragging: (Block?, Offset?) -> Unit,
    /** True finger position in root coordinates; the caller lifts, maps and snaps it. */
    onPlace: (Offset) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var itemPosition by remember { mutableStateOf(Offset.Zero) }
    // Touch point inside the item when the gesture started. Reporting
    // itemPosition alone ignores where the finger grabbed the piece (up to a
    // full item-size error) plus touch slop, so the ghost/drop disagreed with
    // the visible piece by several cells and borderline drops cancelled.
    var touchOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val scaleAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val blockColor = block.colorArgb.toDisplayColor()

    /** Finger position in root coordinates from the tracked gesture. */
    fun fingerRoot(): Offset = itemPosition + touchOffset + dragOffset

    // pointerInput(block.id) never restarts mid-drag, so it would keep calling
    // the onPlace/onDragging lambdas from drag-start (stale dropCell). Always
    // route through the latest callbacks so release uses the current ghost.
    val latestOnDragging by rememberUpdatedState(onDragging)
    val latestOnPlace by rememberUpdatedState(onPlace)
    // Same staleness applies to the measured grid cell size: pointerInput
    // captures the first-composition value (0f, grid not laid out yet) and
    // never refreshes while block.id is unchanged, so the release guard below
    // must read the latest measurement or every drop early-returns and pieces
    // can never land on the board.
    val latestGridCellSize by rememberUpdatedState(gridCellSize)

    Box(
        modifier = Modifier
            .alpha(if (isBeingDragged) 0f else 1f)
            .onGloballyPositioned {
                if (dragOffset == Offset.Zero) itemPosition = it.positionInRoot()
            }
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .scale(scaleAnim.value)
            .pointerInput(block.id) {
                detectDragGestures(onDragStart = { start ->
                    touchOffset = start
                    scope.launch { scaleAnim.animateTo(1.2f) }
                    Log.d(
                        DragLogTag,
                        "dragStart block=${block.id} shape=${block.shape} " +
                            "touch=$touchOffset item=$itemPosition finger=${fingerRoot()}"
                    )
                    latestOnDragging(block, fingerRoot())
                }, onDragEnd = {
                    if (latestGridCellSize <= 0f) {
                        Log.d(
                            DragLogTag,
                            "dragEnd CANCEL block=${block.id} reason=grid-unmeasured " +
                                "finger=${fingerRoot()}"
                        )
                        latestOnDragging(null, null)
                        dragOffset = Offset.Zero
                        touchOffset = Offset.Zero
                        scope.launch { scaleAnim.animateTo(1f) }
                        return@detectDragGestures
                    }

                    Log.d(
                        DragLogTag,
                        "dragEnd block=${block.id} shape=${block.shape} " +
                            "finger=${fingerRoot()} gridCellSize=$latestGridCellSize"
                    )
                    latestOnPlace(fingerRoot())
                    latestOnDragging(null, null)
                    dragOffset = Offset.Zero
                    touchOffset = Offset.Zero
                    scope.launch { scaleAnim.animateTo(1f) }
                }, onDragCancel = {
                    Log.d(DragLogTag, "dragCancel block=${block.id}")
                    latestOnDragging(null, null)
                    dragOffset = Offset.Zero
                    touchOffset = Offset.Zero
                    scope.launch { scaleAnim.animateTo(1f) }
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    dragOffset += dragAmount
                    Log.d(DragLogTag, "dragMove block=${block.id} finger=${fingerRoot()}")
                    latestOnDragging(block, fingerRoot())
                })
            }) {
        val sizeDp = with(density) { (cellSize * 3).toDp() }
        Canvas(modifier = Modifier.size(sizeDp)) {
            val minX = block.shape.minOfOrNull { it.x } ?: 0
            val minY = block.shape.minOfOrNull { it.y } ?: 0
            val maxX = block.shape.maxOfOrNull { it.x } ?: 0
            val maxY = block.shape.maxOfOrNull { it.y } ?: 0
            val shapeWidthCells = (maxX - minX + 1).coerceAtLeast(1)
            val shapeHeightCells = (maxY - minY + 1).coerceAtLeast(1)

            // Keep normal visual size, but scale down if a large shape would overflow this canvas.
            val fitCellSize = min(size.width / shapeWidthCells, size.height / shapeHeightCells)
            val drawCellSize = min(cellSize, fitCellSize)
            val drawOrigin = Offset(
                (size.width - shapeWidthCells * drawCellSize) / 2f - minX * drawCellSize,
                (size.height - shapeHeightCells * drawCellSize) / 2f - minY * drawCellSize
            )
            val inset = min(2.dp.toPx(), drawCellSize * 0.2f)

            block.shape.forEach { coord ->
                val topLeft = Offset(
                    coord.x * drawCellSize + drawOrigin.x,
                    coord.y * drawCellSize + drawOrigin.y
                )
                drawRoundRect(
                    color = blockColor,
                    topLeft = topLeft,
                    size = Size(drawCellSize, drawCellSize),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.5f), // Increased from 0.3f
                    topLeft = topLeft.plus(Offset(inset, inset)),
                    size = Size(
                        (drawCellSize - (2 * inset)).coerceAtLeast(0f),
                        (drawCellSize - (2 * inset)).coerceAtLeast(0f)
                    ),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx()) // Slightly thicker
                )
            }
        }
    }
}

/**
 * A top-level overlay that renders the actively dragged block floating above all other UI.
 * Should be placed at the root Box level of the screen so it is never clipped.
 */
@Composable
fun DraggedBlockOverlay(
    block: Block, offset: Offset, cellSize: Float
) {
    val density = LocalDensity.current
    val scaledCellSize = cellSize * 1.15f
    val sizeDp = with(density) { (scaledCellSize * 4).toDp() }
    val blockColor = block.colorArgb.toDisplayColor()
    Canvas(
        modifier = Modifier
            .size(sizeDp)
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }) {

        block.shape.forEach { coord ->
            val topLeft = Offset(
                coord.x * scaledCellSize,
                coord.y * scaledCellSize,
            )
            // Glow background
            drawRoundRect(
                color = blockColor.copy(alpha = 0.4f),
                topLeft = topLeft,
                size = Size(scaledCellSize, scaledCellSize),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
            // Main body
            drawRoundRect(
                color = blockColor,
                topLeft = topLeft.plus(Offset(2.dp.toPx(), 2.dp.toPx())),
                size = Size(scaledCellSize - 4.dp.toPx(), scaledCellSize - 4.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
            // White border
            drawRoundRect(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = topLeft.plus(Offset(2.dp.toPx(), 2.dp.toPx())),
                size = Size(scaledCellSize - 4.dp.toPx(), scaledCellSize - 4.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF130730)
@Composable
private fun DraggedBlockOverlayAllShapesPreview() {
    GameTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF130730))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tetrisPreviewBlocks().chunked(3).forEach { rowBlocks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowBlocks.forEach { block ->
                        Box(
                            modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center
                        ) {
                            DraggedBlockOverlay(
                                block = block, offset = Offset.Zero, cellSize = 64f
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF130730)
@Composable
private fun BlockItemAllShapesPreview() {
    GameTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF130730))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            tetrisPreviewBlocks().chunked(3).forEach { rowBlocks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowBlocks.forEach { block ->
                        Box(
                            modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center
                        ) {
                            BlockItem(
                                block = block,
                                gridOffset = Offset.Zero,
                                cellSize = 64f,
                                gridCellSize = 64f,
                                onDragging = { _, _ -> },
                                onPlace = { _ -> }
                            )
                        }
                    }
                }
            }
        }
    }
}
