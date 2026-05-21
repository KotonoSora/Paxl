package com.jn.paxl.infrastructure.gameplay

import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.model.Block
import com.jn.paxl.model.ShapeLibrary

object ShapeLibraryBlockCatalog : BlockCatalog {
    override fun randomBlocks(count: Int): List<Block> = ShapeLibrary.getRandomBlocks(count)
}

