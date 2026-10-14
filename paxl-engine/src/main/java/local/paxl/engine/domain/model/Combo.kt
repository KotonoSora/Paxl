package local.paxl.engine.domain.model

/**
 * Block Blast combo/streak multiplier (value object).
 *
 * - [value]: 0 = no active chain; 1+ = multiplier applied to line clears.
 *   The first clear of a chain starts at 1.
 * - [movesSinceClear]: consecutive non-clearing placements since the last
 *   clear. Up to [WINDOW] setup moves keep the chain alive; [RESET_AFTER]
 *   in a row reset it to 0.
 * - [best]: highest combo reached this run (end-of-run stats/UI).
 *
 * The transition lives here so application services never branch on raw
 * combo counters.
 */
data class Combo(
    val value: Int = 0,
    val movesSinceClear: Int = 0,
    val best: Int = 0
) {
    fun onPlacement(cleared: Boolean): Combo {
        if (cleared) {
            val next = if (value <= 0 || movesSinceClear > WINDOW) 1 else value + 1
            return Combo(next, 0, maxOf(best, next))
        }
        val moves = movesSinceClear + 1
        return if (moves >= RESET_AFTER) Combo(0, moves, best) else copy(movesSinceClear = moves)
    }

    companion object {
        /** Setup moves allowed between clears without breaking the chain. */
        const val WINDOW = 2

        /** Consecutive blanks that reset the chain. */
        const val RESET_AFTER = 3
    }
}
