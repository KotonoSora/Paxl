package local.paxl.engine.domain.model

data class LineClearInfo(
    val cellsToClear: Set<Coordinate>,
    val linesCleared: Int
)
