package com.kotonosora.paxl.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kotonosora.paxl.engine.GameEngine
import com.kotonosora.paxl.model.Block
import com.kotonosora.paxl.model.Coordinate
import com.kotonosora.paxl.model.GridState
import com.kotonosora.paxl.ui.theme.NeonCyan
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun GameGrid(
    gridState: GridState,
    draggedBlock: Block?,
    draggedOffset: Offset?,
    gridOffset: Offset,
    onGridMeasured: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NeonGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, // Increased from 0.1f
        targetValue = 0.5f, // Increased from 0.3f
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF130730))
            .onGloballyPositioned {
                val measuredCellSize = it.size.width.toFloat() / gridState.size
                onGridMeasured(it.positionInRoot(), measuredCellSize)
            }
    ) {
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
            gridState.cells.forEach { (coord, color) ->
                if (color != null) {
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

            // Draw Ghost Block Preview (Landing Zone)
            if (draggedBlock != null && draggedOffset != null) {
                val relativeX = draggedOffset.x - gridOffset.x
                val relativeY = draggedOffset.y - gridOffset.y
                val gridX = floor(relativeX / cellSizePx).toInt()
                val gridY = floor(relativeY / cellSizePx).toInt()

                val canPlace = GameEngine.canPlaceBlock(draggedBlock, Coordinate(gridX, gridY), gridState)

                if (canPlace) {
                    draggedBlock.shape.forEach { offset ->
                        val targetTopLeft =
                            Offset((gridX + offset.x) * cellSizePx, (gridY + offset.y) * cellSizePx)
                        drawRoundRect(
                            color = draggedBlock.color.copy(alpha = glowAlpha),
                            topLeft = targetTopLeft,
                            size = Size(cellSizePx, cellSizePx),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )
                        drawRoundRect(
                            color = draggedBlock.color,
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

@Composable
fun BlockItem(
    block: Block,
    gridOffset: Offset,
    cellSize: Float,
    gridCellSize: Float,
    onDragging: (Block?, Offset?) -> Unit,
    onPlace: (Coordinate) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var itemPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val scaleAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .onGloballyPositioned {
                if (dragOffset == Offset.Zero) itemPosition = it.positionInRoot()
            }
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .scale(scaleAnim.value)
            .pointerInput(block.id) {
                detectDragGestures(
                    onDragStart = {
                        scope.launch { scaleAnim.animateTo(1.2f) }
                        onDragging(block, itemPosition + dragOffset)
                    },
                    onDragEnd = {
                        if (gridCellSize <= 0f) {
                            onDragging(null, null)
                            dragOffset = Offset.Zero
                            scope.launch { scaleAnim.animateTo(1f) }
                            return@detectDragGestures
                        }

                        val dropPosition = itemPosition + dragOffset
                        val relativeX = dropPosition.x - gridOffset.x
                        val relativeY = dropPosition.y - gridOffset.y
                        val gridX = floor(relativeX / gridCellSize).toInt()
                        val gridY = floor(relativeY / gridCellSize).toInt()

                        onPlace(Coordinate(gridX, gridY))
                        onDragging(null, null)
                        dragOffset = Offset.Zero
                        scope.launch { scaleAnim.animateTo(1f) }
                    },
                    onDragCancel = {
                        onDragging(null, null)
                        dragOffset = Offset.Zero
                        scope.launch { scaleAnim.animateTo(1f) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                        onDragging(block, itemPosition + dragOffset)
                    }
                )
            }
    ) {
        val sizeDp = with(density) { (cellSize * 3).toDp() }
        Canvas(modifier = Modifier.size(sizeDp)) {
            block.shape.forEach { coord ->
                val topLeft = Offset(coord.x * cellSize, coord.y * cellSize)
                drawRoundRect(
                    color = block.color,
                    topLeft = topLeft,
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.5f), // Increased from 0.3f
                    topLeft = topLeft,
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx()) // Slightly thicker
                )
            }
        }
    }
}
