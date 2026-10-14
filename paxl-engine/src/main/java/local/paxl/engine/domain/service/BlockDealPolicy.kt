package local.paxl.engine.domain.service

import local.paxl.engine.domain.model.Coordinate
import local.paxl.engine.domain.model.TetrominoType

/**
 * Pure domain policy for dealing Tetris pieces.
 *
 * Each tetromino *type* deals with equal probability: a rotation state's
 * weight is inversely proportional to its type's rotation count (single-state
 * O weighs 4x a four-state T/J/L rotation). No randomness here — callers pass
 * a [roll] in `0 until totalWeight` (supplied by infrastructure RNG), which
 * keeps this object deterministic and unit-testable.
 */
object BlockDealPolicy {
    data class Entry(
        val type: TetrominoType,
        val shape: List<Coordinate>,
        val weight: Int
    )

    /** Weight per rotation state so each type deals equally. */
    private const val W4 = 1 // four-state pieces (T, J, L)
    private const val W2 = 2 // two-state pieces (I, S, Z)
    private const val W1 = 4 // single-state piece (O)

    private fun weightFor(type: TetrominoType): Int = when (type) {
        TetrominoType.T, TetrominoType.J, TetrominoType.L -> W4
        TetrominoType.I, TetrominoType.S, TetrominoType.Z -> W2
        TetrominoType.O -> W1
    }

    /** All 19 weighted deal entries, in fixed catalog order. */
    val entries: List<Entry> by lazy {
        TetrominoType.entries.flatMap { type ->
            val weight = weightFor(type)
            local.paxl.engine.domain.model.TetrominoShapes.rotations(type).map { shape ->
                Entry(type, shape, weight)
            }
        }
    }

    val totalWeight: Int by lazy { entries.sumOf { it.weight } }

    /** Pure resolution: which entry a [roll] in `0 until totalWeight` deals. */
    fun resolve(roll: Int): Entry {
        require(roll in 0 until totalWeight) { "roll $roll out of 0 until $totalWeight" }
        var remaining = roll
        for (entry in entries) {
            remaining -= entry.weight
            if (remaining < 0) return entry
        }
        return entries.last()
    }
}
