package local.paxl.engine.domain.model

import java.util.UUID

/**
 * A placeable block. Colors are ARGB longs so this module stays pure Kotlin
 * (no Compose); game-ui converts with `Color(argb.toULong())` at the render
 * boundary.
 */
data class Block(
    val shape: List<Coordinate>,
    val colorArgb: Long,
    val id: String = UUID.randomUUID().toString()
)
