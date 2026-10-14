package local.paxl.engine.domain.service

import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.DragPreview
import local.paxl.engine.domain.model.GridState
import kotlin.math.abs

/** Forgiving drop resolution + drag previews (magnetic Block Blast feel). */
object DropSnapping {
    /** Block Blast default magnetic snap radius, in cells. */
    const val DEFAULT_SNAP_RADIUS = 2

    /** Outer magnet window for the global nearest-fit fallback (see below). */
    const val MAX_MAGNET_RADIUS = 4

    /**
     * Returns the board origin [block] should occupy for a pointer at
     * [pointerCell] (already mapped from the lifted finger anchor to a cell),
     * or null when nothing nearby fits. Single source of truth for BOTH
     * ghost preview and drop:
     * 1. Exact cell when it fits.
     * 2. Edge-clamped cell (near-edge drags stick instead of rejecting).
     * 3. Nearest fittable cell within [snapRadius] (Manhattan nearest,
     *    Euclidean tie-break).
     * 4. Clamp-neighborhood search (wide pieces at edges: the single
     *    clamped cell may be blocked while a free run sits next to it).
     * 5. Global magnetic fallback: nearest valid placement within
     *    [MAX_MAGNET_RADIUS]. Long bars have few valid origins, so a tight
     *    cube around the finger can miss a free run 3-4 cells away while the
     *    finger is still over the grid.
     *
     * Screens must use this for BOTH the ghost AND the drop so what the
     * player sees is what gets placed.
     */
    fun snapToPlacement(
        block: Block,
        pointerCell: Coordinate,
        grid: GridState,
        snapRadius: Int = DEFAULT_SNAP_RADIUS
    ): Coordinate? {
        if (PlacementRules.canPlaceBlock(block, pointerCell, grid)) return pointerCell

        val clamped = Coordinate(
            pointerCell.x.coerceIn(-block.minX, (grid.size - 1 - block.maxX).coerceAtLeast(-block.minX)),
            pointerCell.y.coerceIn(-block.minY, (grid.size - 1 - block.maxY).coerceAtLeast(-block.minY))
        )
        if (clamped != pointerCell && PlacementRules.canPlaceBlock(block, clamped, grid)) {
            return clamped
        }

        nearestInCube(block, pointerCell, grid, snapRadius)?.let { return it }
        if (clamped != pointerCell) {
            nearestInCube(block, clamped, grid, snapRadius)?.let { return it }
        }
        return nearestPlacement(block, pointerCell, grid, MAX_MAGNET_RADIUS)
    }

    /** Nearest valid origin to [pointerCell] inside the radius cube, or null. */
    private fun nearestInCube(
        block: Block,
        pointerCell: Coordinate,
        grid: GridState,
        snapRadius: Int
    ): Coordinate? {
        var best: Coordinate? = null
        var bestManhattan = Int.MAX_VALUE
        var bestEuclid = Int.MAX_VALUE
        for (dy in -snapRadius..snapRadius) {
            for (dx in -snapRadius..snapRadius) {
                if (dx == 0 && dy == 0) continue
                if (abs(dx) + abs(dy) > snapRadius * 2) continue
                val candidate = Coordinate(pointerCell.x + dx, pointerCell.y + dy)
                if (!PlacementRules.canPlaceBlock(block, candidate, grid)) continue
                val manhattan = abs(dx) + abs(dy)
                val euclid = dx * dx + dy * dy
                if (manhattan < bestManhattan ||
                    (manhattan == bestManhattan && euclid < bestEuclid)
                ) {
                    bestManhattan = manhattan
                    bestEuclid = euclid
                    best = candidate
                }
            }
        }
        return best
    }

    /**
     * Nearest valid origin to [pointerCell] within [maxRadius] (Manhattan),
     * or null when no fit is near. Backs step 5 of [snapToPlacement].
     */
    fun nearestPlacement(
        block: Block,
        pointerCell: Coordinate,
        grid: GridState,
        maxRadius: Int = MAX_MAGNET_RADIUS
    ): Coordinate? {
        var best: Coordinate? = null
        var bestManhattan = Int.MAX_VALUE
        var bestEuclid = Int.MAX_VALUE
        for (origin in PlacementRules.findPlacements(block, grid)) {
            val manhattan = abs(origin.x - pointerCell.x) + abs(origin.y - pointerCell.y)
            if (manhattan > maxRadius) continue
            val dx = origin.x - pointerCell.x
            val dy = origin.y - pointerCell.y
            val euclid = dx * dx + dy * dy
            val isBetter = manhattan < bestManhattan ||
                (manhattan == bestManhattan && euclid < bestEuclid) ||
                (manhattan == bestManhattan && euclid == bestEuclid &&
                    (best == null || origin.y < best.y ||
                        (origin.y == best.y && origin.x < best.x)))
            if (isBetter) {
                bestManhattan = manhattan
                bestEuclid = euclid
                best = origin
            }
        }
        return best
    }

    /**
     * Full drag preview for one frame: snapped origin (or null), ghost cells,
     * and the lines that WOULD clear if dropped there. UI should render:
     * - ghost cells at 40-60% alpha + solid border when [DragPreview.isValid],
     * - [DragPreview.wouldClear] rows/cols highlighted (Block Blast "blast"
     *   glow), red conflict tint when over-grid but invalid.
     */
    fun previewDrop(
        block: Block,
        pointerCell: Coordinate,
        grid: GridState,
        snapRadius: Int = DEFAULT_SNAP_RADIUS
    ): DragPreview {
        val overGrid = block.cellsAt(pointerCell).any { c ->
            c.x in 0 until grid.size && c.y in 0 until grid.size
        }
        val origin = snapToPlacement(block, pointerCell, grid, snapRadius)
            ?: return DragPreview(
                block = block,
                origin = null,
                gridSize = grid.size,
                isOverGrid = overGrid
            )
        val ghostCells = block.cellsAt(origin).toSet()
        val simulated = grid.cells.toMutableMap()
        block.shape.forEach { offset ->
            simulated[Coordinate(origin.x + offset.x, origin.y + offset.y)] = block.colorArgb
        }
        val clear = LineClearing.findLineClearInfo(simulated, grid.size)
        return DragPreview(
            block = block,
            origin = origin,
            cells = ghostCells,
            wouldClear = clear?.cellsToClear.orEmpty(),
            wouldClearLines = clear?.linesCleared ?: 0,
            gridSize = grid.size,
            isOverGrid = true
        )
    }
}
