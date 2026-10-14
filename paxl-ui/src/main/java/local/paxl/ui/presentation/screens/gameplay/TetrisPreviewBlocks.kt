package local.paxl.ui.presentation.screens.gameplay

import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.TetrominoType

/**
 * Preview/showcase blocks for `@Preview` and empty-tray placeholders
 * (presentation concern — explicit stable ids, classic colors read from the
 * domain [TetrominoType] vocabulary).
 */
fun tetrisPreviewBlocks(): List<Block> {
    fun c(x: Int, y: Int) = Coordinate(x, y)
    val i = TetrominoType.I.colorArgb
    val o = TetrominoType.O.colorArgb
    val t = TetrominoType.T.colorArgb
    val s = TetrominoType.S.colorArgb
    val z = TetrominoType.Z.colorArgb
    val j = TetrominoType.J.colorArgb
    val l = TetrominoType.L.colorArgb
    return listOf(
        Block(listOf(c(0, 0), c(1, 0), c(2, 0), c(3, 0)), i, "preview-block-0"), // I
        Block(listOf(c(0, 0), c(1, 0), c(0, 1), c(1, 1)), o, "preview-block-1"), // O
        Block(listOf(c(0, 0), c(1, 0), c(2, 0), c(1, 1)), t, "preview-block-2"), // T
        Block(listOf(c(1, 0), c(2, 0), c(0, 1), c(1, 1)), s, "preview-block-3"), // S
        Block(listOf(c(0, 0), c(1, 0), c(1, 1), c(2, 1)), z, "preview-block-4"), // Z
        Block(listOf(c(0, 0), c(1, 0), c(2, 0), c(0, 1)), j, "preview-block-5"), // J
        Block(listOf(c(0, 0), c(1, 0), c(2, 0), c(2, 1)), l, "preview-block-6"), // L
        Block(listOf(c(0, 0), c(0, 1), c(0, 2), c(0, 3)), i, "preview-block-7"), // I vertical
        Block(listOf(c(1, 0), c(0, 1), c(1, 1), c(2, 1)), t, "preview-block-8") // T up
    )
}
