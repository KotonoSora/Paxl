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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository {
        return DataStoreRepository(context)
    }

    @Provides
    @Singleton
    fun provideBillingRepository(
        @ApplicationContext context: Context,
        dataStoreRepository: DataStoreRepository
    ): BillingRepository {
        return BillingRepository(context, dataStoreRepository)
    }

    @Provides
    @Singleton
    fun provideGameplayUseCases(): GameplayUseCases {
        val catalog = ShapeLibraryBlockCatalog
        return GameplayUseCases(
            startNewGame = StartNewGameUseCase(catalog),
            placeBlock = PlaceBlockUseCase(catalog),
            undoMove = UndoMoveUseCase(),
            reshuffleBlocks = ReshuffleBlocksUseCase(catalog)
        )
    }

    @Provides
    @Singleton
    fun provideShopUseCases(): ShopUseCases {
        return ShopUseCases(
            observeShopUiState = ObserveShopUiStateUseCase()
        )
    }
}
