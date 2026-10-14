package local.paxl.ui.application

import kotlinx.coroutines.flow.first
import local.core.ui.data.CoinAccount
import local.paxl.engine.domain.model.RunRecord
import local.paxl.engine.domain.repository.RunHistoryRepository

/**
 * Records a finished game: coin reward + run-history row.
 * High score is derived from runs (core), so this returns whether [record]
 * set a new high (for the win sound / highlight).
 */
class RecordGameFinishedUseCase(
    private val coinAccount: CoinAccount,
    private val runs: RunHistoryRepository
) {
    suspend operator fun invoke(record: RunRecord): Boolean {
        val previousHigh = runs.highScoreFlow.first()

        if (record.reward > 0) {
            coinAccount.addCoins(record.reward)
        }
        runs.recordRun(record)

        return record.score > 0 && record.score > previousHigh
    }
}
