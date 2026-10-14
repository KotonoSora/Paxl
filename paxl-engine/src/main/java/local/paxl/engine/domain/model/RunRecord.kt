package local.paxl.engine.domain.model

/**
 * One played game, engine-owned. Field-for-field compatible with core's
 * RunRecord so the ui adapters map without a Room migration (Option A):
 * - mode = PlayMode name (CLASSIC/LEVELS/DAILY)
 * - level = current level (LEVELS mode) else 1
 * - pipesPassed = lines cleared this run (repurposed Flappy field, no schema change)
 * - shieldUses = undo uses, multiplierUses = reshuffle uses
 * - autoPlayUses = 0 (Paxl has no third consumable; continue is pre-run)
 * - deathX/deathY always null (no death position in Paxl)
 */
data class RunRecord(
    val mode: String,
    val level: Int,
    val score: Int,
    val reward: Int,
    val durationMs: Long,
    val pipesPassed: Int,
    val endCause: RunEndCause,
    val shieldUses: Int = 0,
    val multiplierUses: Int = 0,
    val autoPlayUses: Int = 0,
    val deathX: Float? = null,
    val deathY: Float? = null,
    val startedAt: Long,
    val endedAt: Long
)
