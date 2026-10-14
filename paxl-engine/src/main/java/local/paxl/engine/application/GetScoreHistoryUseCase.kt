package local.paxl.engine.application

import kotlinx.coroutines.flow.Flow
import local.paxl.engine.domain.model.RunRecord
import local.paxl.engine.domain.repository.RunHistoryRepository

class GetScoreHistoryUseCase(private val runs: RunHistoryRepository) {
    operator fun invoke(limit: Int = 50): Flow<List<RunRecord>> = runs.recentRunsFlow(limit)
}
