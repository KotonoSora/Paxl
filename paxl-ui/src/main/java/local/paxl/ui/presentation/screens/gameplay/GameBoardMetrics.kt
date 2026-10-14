package local.paxl.ui.presentation.screens.gameplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Layout policy for the gameplay screen (presentation layer).
 *
 * Two independent rules:
 * - Board: fixed from device width ([boardSize]), never from layout remainder,
 *   so showing/hiding COMBO can't resize it mid-game.
 * - Chrome (everything else): all rows use FIXED heights and every text sets
 *   an explicit `lineHeight`, so the chrome total is exact arithmetic
 *   ([CHROME_UNITS] × [chromeScale]) rather than font-metric guesswork. When
 *   the measured height can't fit board + full-scale chrome, only the chrome
 *   compacts — the board never shrinks.
 *
 * [gameBoardMetrics] is pure (Dp in, data out) and unit-testable;
 * [rememberGameBoardMetrics] is the thin Composable adapter fed by the
 * post-Scaffold-padding `BoxWithConstraints` (exact budget, no inset math).
 */
@Immutable
data class GameBoardMetrics(
    val boardSize: Dp,
    /** Scale for the board (reference 360.dp ≈ typical phone board). */
    val uiScale: Float,
    /** Scale for all surrounding UI; ≤ [uiScale], compacts on short screens. */
    val chromeScale: Float,
    val levelFontSize: TextUnit,
    val levelLineHeight: TextUnit,
    val scoreFontSize: TextUnit,
    val scoreLineHeight: TextUnit,
    val comboFontSize: TextUnit,
    val comboLineHeight: TextUnit,
    val comboSlotHeight: Dp,
    val headerRowHeight: Dp,
    val timerFontSize: TextUnit,
    val timerLineHeight: TextUnit,
    val timerRowHeight: Dp,
    val coinIconSize: Dp,
    val coinFontSize: TextUnit,
    val coinLineHeight: TextUnit,
    val goalFontSize: TextUnit,
    val goalLineHeight: TextUnit,
    val goalBoxHeight: Dp,
    val sectionGap: Dp,
    val headerVerticalPadding: Dp,
    val powerButtonSize: Dp,
    val powerLabelSize: TextUnit,
    val powerLabelLineHeight: TextUnit,
    val powerRowHeight: Dp,
    val powerVerticalPadding: Dp,
    val powerContentGap: Dp,
    val noticeSlotHeight: Dp,
    val noticeFontSize: Int,
    val trayHeight: Dp,
    val trayItemSize: Dp,
    val trayBottomPadding: Dp,
    /** Tray piece cells (chrome scale, must fit the compacted tray). */
    val previewCellSize: Dp,
    /**
     * Dragged-overlay piece cells (board scale). The overlay floats over the
     * BOARD, so its cells must stay proportional to board cells — otherwise
     * the player cannot judge which grid cells the piece covers and tight-gap
     * drops become guesswork.
     */
    val dragCellSize: Dp
) {
    companion object {
        internal val REFERENCE_BOARD = 360.dp
        internal val MIN_BOARD = 240.dp
        internal val MAX_BOARD = 480.dp

        /**
         * Exact chrome height in dp-units at scale 1: header row
         * (24 pad + 24 level + 29 score + 20 combo = 97) + timer row
         * (24 pad + 37 = 61) + goal box (44) + gap (16) + power row
         * (32 pad + 56 button + 4 gap + 16 label = 108) + notices (44) +
         * tray (130) = 500. Line slots (1.3× font) always contain the glyphs,
         * so this is an exact upper bound of the laid-out chrome.
         */
        internal const val CHROME_UNITS = 500f

        /** Overflow absorbed without compacting (legacy phone budget). */
        internal val OVERFLOW_TOLERANCE = 48.dp

        /** Floor so chrome never compacts into unreadability. */
        internal const val MIN_CHROME_SCALE = 0.5f
    }
}

fun gameBoardMetrics(availableWidth: Dp, availableHeight: Dp): GameBoardMetrics {
    // Board: width-based only (legacy logic, unchanged sizing).
    val boardSize = minOf(
        availableWidth - 32.dp,
        availableHeight * 0.55f
    ).coerceIn(GameBoardMetrics.MIN_BOARD, GameBoardMetrics.MAX_BOARD)
    val uiScale = (boardSize.value / GameBoardMetrics.REFERENCE_BOARD.value)
        .coerceIn(0.75f, 1.3f)

    // Chrome: full scale when it fits (within tolerance), else the exact scale
    // that closes the gap: chromeScale = (availH − board) / 500.
    val chromeAvail = availableHeight - boardSize
    val fullChrome = GameBoardMetrics.CHROME_UNITS.dp * uiScale
    val chromeScale = if (fullChrome <= chromeAvail + GameBoardMetrics.OVERFLOW_TOLERANCE) {
        uiScale
    } else {
        (chromeAvail.value / GameBoardMetrics.CHROME_UNITS)
            .coerceAtLeast(GameBoardMetrics.MIN_CHROME_SCALE)
            .coerceAtMost(uiScale)
    }
    val c = chromeScale
    return GameBoardMetrics(
        boardSize = boardSize,
        uiScale = uiScale,
        chromeScale = c,
        levelFontSize = (18f * c).sp,
        levelLineHeight = (24f * c).sp,
        scoreFontSize = (22f * c).sp,
        scoreLineHeight = (29f * c).sp,
        comboFontSize = (14f * c).sp,
        comboLineHeight = (20f * c).sp,
        comboSlotHeight = 20.dp * c,
        headerRowHeight = 97.dp * c,
        timerFontSize = (28f * c).sp,
        timerLineHeight = (37f * c).sp,
        timerRowHeight = 61.dp * c,
        coinIconSize = 24.dp * c,
        coinFontSize = (20f * c).sp,
        coinLineHeight = (26f * c).sp,
        goalFontSize = (14f * c).sp,
        goalLineHeight = (19f * c).sp,
        goalBoxHeight = 44.dp * c,
        sectionGap = 16.dp * c,
        headerVerticalPadding = 12.dp * c,
        powerButtonSize = 56.dp * c,
        powerLabelSize = (12f * c).sp,
        powerLabelLineHeight = (16f * c).sp,
        powerRowHeight = 108.dp * c,
        powerVerticalPadding = 16.dp * c,
        powerContentGap = 4.dp * c,
        noticeSlotHeight = 22.dp * c,
        noticeFontSize = (12f * c).roundToInt(),
        trayHeight = 130.dp * c,
        trayItemSize = 90.dp * c,
        trayBottomPadding = 12.dp * c,
        previewCellSize = 24.dp * c,
        dragCellSize = 24.dp * uiScale
    )
}

@Composable
fun rememberGameBoardMetrics(availableWidth: Dp, availableHeight: Dp): GameBoardMetrics {
    return remember(availableWidth, availableHeight) {
        gameBoardMetrics(availableWidth, availableHeight)
    }
}
