package local.paxl.engine.domain.model

/**
 * The 7 Tetris tetromino types (ubiquitous language).
 *
 * Guideline [colorArgb] is domain vocabulary here, not a UI leak: dealing a
 * piece "in its classic color" is a game rule (like a card suit's color).
 * Colors stay ARGB longs so the engine remains pure Kotlin; UI converts to
 * Compose `Color` at the render boundary.
 */
enum class TetrominoType(val colorArgb: Long) {
    I(0xFF00E5FFL), // cyan
    O(0xFFFFD600L), // yellow
    T(0xFFAA00FFL), // purple
    S(0xFF00E676L), // green
    Z(0xFFFF3D00L), // red
    J(0xFF2979FFL), // blue
    L(0xFFFF9100L); // orange
}
