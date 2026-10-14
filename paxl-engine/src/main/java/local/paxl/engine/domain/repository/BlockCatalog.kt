package local.paxl.engine.domain.repository

import local.paxl.engine.domain.model.Block

/**
 * Dealing port (ubiquitous language: the tray is *dealt*, not "randomized").
 * Infrastructure adapters supply the randomness; application use cases depend
 * only on this port.
 */
fun interface BlockCatalog {
    fun deal(count: Int): List<Block>
}
