package local.paxl.engine.domain.model

/**
 * Engine-owned game state (pure Kotlin, no Compose/Android). Coins, audio
 * flags and high score live in core flows and are combined at the UI layer;
 * only run-scoped gameplay state lives here.
 */
data class GameState(
    val grid: GridState = GridState(),
    val availableBlocks: List<Block> = emptyList(),
    val currentLevel: Int = 1,
    val playMode: PlayMode = PlayMode.CLASSIC,
    val targetScore: Int = 5000,
    val winReward: Int = 25,
    val sessionStartMs: Long = System.currentTimeMillis(),
    val score: Int = 0,
    val linesCleared: Int = 0,
    val isGameOver: Boolean = false,
    val isWin: Boolean = false,
    val isWinConditionSkipped: Boolean = false,
    val isClearing: Boolean = false,
    val clearAnimationId: Long = 0L,
    val clearingCells: Map<Coordinate, Long?> = emptyMap()
)
