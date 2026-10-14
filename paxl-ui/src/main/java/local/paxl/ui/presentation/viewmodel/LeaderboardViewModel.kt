package local.paxl.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import local.paxl.engine.application.GetScoreHistoryUseCase
import local.paxl.engine.domain.model.RunRecord

class LeaderboardViewModel(
    getScoreHistory: GetScoreHistoryUseCase
) : ViewModel() {

    val leaderboard: StateFlow<List<RunRecord>> = getScoreHistory(100)
        .map { sortLeaderboard(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun sortLeaderboard(entries: List<RunRecord>): List<RunRecord> {
        return entries.sortedWith(
            compareByDescending<RunRecord> { it.score }
                .thenBy { it.durationMs }
                .thenByDescending { it.endedAt }
        )
    }
}
