package local.paxl.engine.application

import local.paxl.engine.domain.model.GameState
import local.paxl.engine.domain.model.GridState
import local.paxl.engine.domain.model.PlayMode
import local.paxl.engine.domain.repository.BlockCatalog
import local.paxl.engine.domain.service.RewardPolicy

class StartNewGameUseCase(
    private val blockCatalog: BlockCatalog,
    private val gridSize: Int = 10
) {
    operator fun invoke(
        level: Int = 1,
        mode: PlayMode = PlayMode.CLASSIC
    ): GameState {
        val reward = RewardPolicy.forMode(mode, level)

        return GameState(
            grid = GridState(size = gridSize),
            availableBlocks = blockCatalog.randomBlocks(3),
            currentLevel = level,
            playMode = mode,
            targetScore = reward.targetScore,
            winReward = reward.winReward,
            sessionStartMs = System.currentTimeMillis()
        )
    }
}
