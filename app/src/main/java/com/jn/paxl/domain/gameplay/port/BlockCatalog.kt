package com.jn.paxl.domain.gameplay.port

import com.jn.paxl.model.Block

interface BlockCatalog {
    fun randomBlocks(count: Int): List<Block>
}

