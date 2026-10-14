package local.paxl.ui.presentation.screens

import local.core.shop.coins.CoinPackItem
import local.paxl.engine.domain.model.RunEndCause
import local.paxl.engine.domain.model.RunRecord
import local.paxl.shop.application.CoinShopConfig
import local.paxl.shop.domain.service.TokenPacks

object PreviewData {
    val mockCoinPacks: List<CoinPackItem> = TokenPacks.mockProducts.map {
        CoinShopConfig.coinPackItem(it)
    }

    private val now = System.currentTimeMillis()
    val fakeRuns = listOf(
        RunRecord(
            mode = "CLASSIC", level = 1, score = 15000, reward = 25, durationMs = 180_000,
            pipesPassed = 6, endCause = RunEndCause.WIN, shieldUses = 1,
            startedAt = now - 3_600_000, endedAt = now - 3_600_000
        ),
        RunRecord(
            mode = "LEVELS", level = 5, score = 15000, reward = 0, durationMs = 200_000,
            pipesPassed = 4, endCause = RunEndCause.GAME_OVER, multiplierUses = 1,
            startedAt = now - 7_200_000, endedAt = now - 7_200_000
        ),
        RunRecord(
            mode = "DAILY", level = 1, score = 12000, reward = 0, durationMs = 170_000,
            pipesPassed = 3, endCause = RunEndCause.GAME_OVER,
            startedAt = now - 10_800_000, endedAt = now - 10_800_000
        )
    )
}
