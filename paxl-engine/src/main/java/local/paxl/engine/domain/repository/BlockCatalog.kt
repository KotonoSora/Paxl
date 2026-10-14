package local.paxl.engine.domain.repository

import local.paxl.engine.domain.model.Block

fun interface BlockCatalog {
    fun randomBlocks(count: Int): List<Block>
}
