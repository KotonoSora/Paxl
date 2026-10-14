package local.paxl.engine.domain.model

/**
 * One frame of Block Blast drag feedback, computed by
 * `DropSnapping.previewDrop` (pure engine, no Compose).
 *
 * - [origin]: snapped board origin, null = no fit near the finger.
 * - [cells]: ghost cells the piece would occupy (empty when invalid).
 * - [wouldClear]: full rows/cols that would blast on drop (for the
 *   highlight glow). [wouldClearLines]: their count.
 * - [isOverGrid]: whether the raw pointer maps onto the board at all;
 *   UI shows red conflict tint when over-grid but invalid.
 */
data class DragPreview(
    val block: Block,
    val origin: Coordinate?,
    val cells: Set<Coordinate> = emptySet(),
    val wouldClear: Set<Coordinate> = emptySet(),
    val wouldClearLines: Int = 0,
    val gridSize: Int = GridState.DEFAULT_SIZE,
    val isOverGrid: Boolean = false
) {
    val isValid: Boolean get() = origin != null
}
