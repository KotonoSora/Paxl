package com.jn.paxl.di

import android.content.Context
import com.jn.paxl.application.gameplay.GameplayUseCases
import com.jn.paxl.application.gameplay.PlaceBlockUseCase
import com.jn.paxl.application.gameplay.ReshuffleBlocksUseCase
import com.jn.paxl.application.gameplay.StartNewGameUseCase
import com.jn.paxl.application.gameplay.UndoMoveUseCase
import com.jn.paxl.application.shop.ObserveShopUiStateUseCase
import com.jn.paxl.application.shop.ShopUseCases
import com.jn.paxl.infrastructure.gameplay.ShapeLibraryBlockCatalog
import com.jn.paxl.repository.BillingRepository
import com.jn.paxl.repository.DataStoreRepository

interface AppContainer {
    val dataStoreRepository: DataStoreRepository
    val billingRepository: BillingRepository
    val gameplayUseCases: GameplayUseCases
    val shopUseCases: ShopUseCases
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val dataStoreRepository: DataStoreRepository by lazy {
        DataStoreRepository(context)
    }

    override val billingRepository: BillingRepository by lazy {
        BillingRepository(context, dataStoreRepository)
    }

    override val gameplayUseCases: GameplayUseCases by lazy {
        val catalog = ShapeLibraryBlockCatalog
        GameplayUseCases(
            startNewGame = StartNewGameUseCase(catalog),
            placeBlock = PlaceBlockUseCase(catalog),
            undoMove = UndoMoveUseCase(),
            reshuffleBlocks = ReshuffleBlocksUseCase(catalog)
        )
    }

    override val shopUseCases: ShopUseCases by lazy {
        ShopUseCases(
            observeShopUiState = ObserveShopUiStateUseCase()
        )
    }
}
