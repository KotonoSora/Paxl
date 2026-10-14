package local.paxl.engine.domain.service

import local.paxl.engine.domain.model.PlayMode

/**
 * Block Blast scoring/reward rules (pure Kotlin).
 *
 * Move score (verified against Block Blast web + app breakdowns):
 * ```
 * gain = placedCells * PLACEMENT_PER_CELL
 *      + (clearedCells * CLEAR_PER_CELL + lineBonus(lines)) * combo
 *      + (ALL_CLEAR_BONUS if board emptied else 0)
 * ```
 * - [PLACEMENT_PER_CELL] = 1 per placed cell (not multiplied).
 * - [CLEAR_PER_CELL] = 10 per cleared cell (12-cell line = 120 base).
 * - [lineBonus] escalates with simultaneous lines: 1->10, 2->20, 3->60,
 *   4->120, 5->200, 6+->300.
 * - [combo]: first clear = 1, consecutive clears within 2 setup moves raise
 *   it by 1 per clearing placement (multi-line still +1, not +lines).
 * - [ALL_CLEAR_BONUS] = 360 when a clear leaves the board completely empty.
 */
object RewardPolicy {
    const val PLACEMENT_PER_CELL = 1
    const val CLEAR_PER_CELL = 10
    const val ALL_CLEAR_BONUS = 360

    /** Escalating bonus for clearing [lines] at once. */
    fun lineBonus(lines: Int): Int = when {
        lines <= 0 -> 0
        lines == 1 -> 10
        lines == 2 -> 20
        lines == 3 -> 60
        lines == 4 -> 120
        lines == 5 -> 200
        else -> 300
    }

    /** Line-clear portion (before adding placement points / all-clear). */
    fun clearScore(clearedCells: Int, lines: Int, combo: Int): Int {
        if (lines <= 0 || clearedCells <= 0 || combo <= 0) return 0
        return (clearedCells * CLEAR_PER_CELL + lineBonus(lines)) * combo
    }

    fun moveScore(
        placedCells: Int,
        clearedCells: Int,
        lines: Int,
        combo: Int,
        isAllClear: Boolean
    ): Int {
        val placement = placedCells * PLACEMENT_PER_CELL
        val clear = clearScore(clearedCells, lines, combo)
        val allClear = if (isAllClear && lines > 0) ALL_CLEAR_BONUS else 0
        return placement + clear + allClear
    }

    data class ModeReward(val targetScore: Int, val winReward: Int)

    /**
     * Win target and coin reward per [PlayMode]. Targets are tuned for the
     * economy above on a 12x12 board: an average placement pays ~4-5, a
     * single line at combo 1 pays ~130, so CLASSIC 5000 lands after roughly
     * a hundred good moves with combo chains — the same order of magnitude
     * as the old 10/cell economy, now weighted toward clears over spam.
     */
    fun forMode(mode: PlayMode, level: Int): ModeReward = when (mode) {
        PlayMode.CLASSIC -> ModeReward(5000, 25)
        PlayMode.LEVELS -> ModeReward(700 + (level * 350), 10 + (level * 2))
        PlayMode.DAILY -> ModeReward(5000, 50)
    }
}
