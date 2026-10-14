package local.paxl.engine.domain.service

import local.paxl.engine.domain.model.PlayMode

/**
 * Pure scoring/reward rules: points per placed cell and per cleared line,
 * plus the win target and coin reward per [PlayMode].
 */
object RewardPolicy {
    const val SCORE_PER_CELL = 10
    const val SCORE_PER_LINE = 100

    data class ModeReward(val targetScore: Int, val winReward: Int)

    fun forMode(mode: PlayMode, level: Int): ModeReward = when (mode) {
        PlayMode.CLASSIC -> ModeReward(5000, 25)
        PlayMode.LEVELS -> ModeReward(700 + (level * 350), 10 + (level * 2))
        PlayMode.DAILY -> ModeReward(5000, 50)
    }
}
