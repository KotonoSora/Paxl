package local.paxl.engine.infrastructure.dealing

import local.paxl.engine.domain.model.Block
import local.paxl.engine.domain.repository.BlockCatalog
import local.paxl.engine.domain.repository.BlockIdGenerator
import local.paxl.engine.domain.service.BlockDealPolicy
import java.util.UUID
import kotlin.random.Random

/**
 * Infrastructure adapter: deals Tetris pieces from [BlockDealPolicy] using
 * platform randomness. The only engine piece allowed to depend on
 * [kotlin.random.Random] and [UUID].
 */
class TetrisBlockCatalog(
    private val random: Random = Random.Default,
    private val idGenerator: BlockIdGenerator = UuidBlockIdGenerator()
) : BlockCatalog {

    override fun deal(count: Int): List<Block> {
        require(count >= 0) { "count must be >= 0, was $count" }
        return (1..count).map { dealOne() }
    }

    private fun dealOne(): Block {
        val entry = BlockDealPolicy.resolve(random.nextInt(BlockDealPolicy.totalWeight))
        return Block(
            shape = entry.shape,
            colorArgb = entry.type.colorArgb,
            id = idGenerator.generate()
        )
    }

    private class UuidBlockIdGenerator : BlockIdGenerator {
        override fun generate(): String = UUID.randomUUID().toString()
    }
}
