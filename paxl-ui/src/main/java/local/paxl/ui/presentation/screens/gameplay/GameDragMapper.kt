package local.paxl.ui.presentation.screens.gameplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import kotlin.math.floor

/**
 * Pointer → board-cell mapping for drag & drop (presentation adapter).
 *
 * Pure functions so the math is unit-testable without Compose. The single
 * placement source of truth stays in the engine
 * ([local.paxl.engine.domain.service.DropSnapping]): the piece floats
 * `DragLiftDp` above the finger and the lifted anchor maps to a cell that
 * snaps (edge clamp + nearest fit). The same snapped cell drives ghost AND
 * drop.
 */
object GameDragMapper {
    /**
     * Drop zone: grid plus a 1-cell margin, preserving near-edge magnet
     * forgiveness. Releasing outside it cancels the drag.
     */
    fun isInDropZone(cell: Coordinate, boardSize: Int): Boolean =
        cell.x >= -1 && cell.y >= -1 && cell.x <= boardSize && cell.y <= boardSize

    /**
     * Maps a root-coordinate [pointer] to a board cell. Returns null only when
     * [cellSizePx] is not measured yet (<= 0).
     */
    fun pointerCellFor(
        pointer: Offset,
        gridOffset: Offset,
        cellSizePx: Float
    ): Coordinate? {
        if (cellSizePx <= 0f) return null
        return Coordinate(
            floor((pointer.x - gridOffset.x) / cellSizePx).toInt(),
            floor((pointer.y - gridOffset.y) / cellSizePx).toInt()
        )
    }
}

/**
 * Drag-gesture state holder. Keeps the mutable drag session (piece, finger,
 * grid measurement) out of the Composable body so the screen renders state
 * instead of owning pointer math.
 */
@Stable
class GameDragState {
    var draggedBlock by mutableStateOf<Block?>(null)
    var draggedOffset by mutableStateOf<Offset?>(null)
    var gridOffset by mutableStateOf(Offset.Zero)
    var gridCellSizePx by mutableFloatStateOf(0f)

    /**
     * Set on Back-press mid-drag; further drag events for the in-flight
     * gesture are ignored until the finger lifts.
     */
    var dragCancelled by mutableStateOf(false)

    fun onGridMeasured(offset: Offset, measuredCellSizePx: Float) {
        if (gridOffset != offset) gridOffset = offset
        if (kotlin.math.abs(gridCellSizePx - measuredCellSizePx) > 0.5f) {
            gridCellSizePx = measuredCellSizePx
        }
    }

    fun cancel() {
        dragCancelled = true
        draggedBlock = null
        draggedOffset = null
    }

    fun endGesture() {
        dragCancelled = false
        draggedBlock = null
        draggedOffset = null
    }
}

@Composable
fun rememberGameDragState(): GameDragState = remember { GameDragState() }
