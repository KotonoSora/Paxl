package local.paxl.ui.di

import android.content.Context
import local.core.billing.BillingConfig
import local.core.billing.BillingManagerImpl
import local.core.billing.BillingRepository
import local.core.billing.MockBillingManager
import local.core.ui.data.AudioSettings
import local.core.ui.data.CoinAccount
import local.core.ui.data.RoomRunStore
import local.core.ui.data.RunStore
import local.core.ui.data.SettingsRepositoryImpl
import local.core.ui.data.db.GameDatabase
import local.core.ui.data.db.createGameDatabase
import local.core.ui.usecase.GetAudioSettingsUseCase
import local.core.ui.usecase.ToggleAudioUseCase
import local.paxl.engine.application.GetScoreHistoryUseCase
import local.paxl.engine.application.PlaceBlockUseCase
import local.paxl.engine.application.ReshuffleBlocksUseCase
import local.paxl.engine.application.StartNewGameUseCase
import local.paxl.engine.application.UndoMoveUseCase
import local.paxl.engine.domain.model.BlockShapes
import local.paxl.engine.domain.repository.BlockCatalog
import local.paxl.engine.domain.repository.RunHistoryRepository
import local.paxl.shop.domain.model.SkinIds
import local.paxl.shop.domain.service.TokenPacks
import local.paxl.shop.domain.model.TokenPackIds
import local.paxl.shop.infrastructure.persistence.PaxlInventoryKeys
import local.paxl.ui.adapters.asCoinSink
import local.paxl.ui.adapters.asRunHistoryRepository
import local.paxl.ui.application.RecordGameFinishedUseCase

/**
 * Dependency Injection container for the application. Manual DI, no Hilt/Koin.
 */
interface AppContainer {
    val coinAccount: CoinAccount
    val audioSettings: AudioSettings
    val runHistoryRepository: RunHistoryRepository
    val billingRepository: BillingRepository

    val startNewGameUseCase: StartNewGameUseCase
    val placeBlockUseCase: PlaceBlockUseCase
    val undoMoveUseCase: UndoMoveUseCase
    val reshuffleBlocksUseCase: ReshuffleBlocksUseCase
    val getScoreHistoryUseCase: GetScoreHistoryUseCase
    val getAudioSettingsUseCase: GetAudioSettingsUseCase
    val toggleAudioUseCase: ToggleAudioUseCase
    val recordGameFinishedUseCase: RecordGameFinishedUseCase
}

/**
 * Default implementation of the [AppContainer] interface.
 */
class DefaultAppContainer(
    private val context: Context,
    private val isDebug: Boolean
) : AppContainer {

    /**
     * Single DataStore-backed store, exposed through narrow ports so each
     * consumer depends only on what it uses. Paxl is coin-direct (no
     * inventory), so the injected inventory map is empty; core keeps its
     * frozen keys untouched.
     */
    private val settingsStore: SettingsRepositoryImpl by lazy {
        SettingsRepositoryImpl(
            context,
            SkinIds.SKIN_DEFAULT_ID,
            PaxlInventoryKeys.map()
        )
    }

    override val coinAccount: CoinAccount by lazy { settingsStore }
    override val audioSettings: AudioSettings by lazy { settingsStore }

    private val blockCatalog: BlockCatalog by lazy {
        BlockCatalog { count -> BlockShapes.randomBlocks(count) }
    }

    private val gameDatabase: GameDatabase by lazy {
        createGameDatabase(context)
    }

    private val runStore: RunStore by lazy {
        RoomRunStore(gameDatabase.runDao())
    }

    override val runHistoryRepository: RunHistoryRepository by lazy {
        runStore.asRunHistoryRepository()
    }

    override val billingRepository: BillingRepository by lazy {
        val wallet = coinAccount.asCoinSink()
        if (isDebug) {
            MockBillingManager(wallet, TokenPacks.mockProducts, TokenPacks::getTokenAmount)
        } else {
            BillingManagerImpl(
                context,
                wallet,
                BillingConfig(TokenPackIds.ALL, TokenPacks::getTokenAmount)
            )
        }
    }

    override val startNewGameUseCase: StartNewGameUseCase by lazy {
        StartNewGameUseCase(blockCatalog)
    }
    override val placeBlockUseCase: PlaceBlockUseCase by lazy {
        PlaceBlockUseCase(blockCatalog)
    }
    override val undoMoveUseCase: UndoMoveUseCase by lazy { UndoMoveUseCase() }
    override val reshuffleBlocksUseCase: ReshuffleBlocksUseCase by lazy {
        ReshuffleBlocksUseCase(blockCatalog)
    }

    override val getScoreHistoryUseCase: GetScoreHistoryUseCase by lazy {
        GetScoreHistoryUseCase(runHistoryRepository)
    }
    override val getAudioSettingsUseCase: GetAudioSettingsUseCase by lazy {
        GetAudioSettingsUseCase(audioSettings)
    }
    override val toggleAudioUseCase: ToggleAudioUseCase by lazy {
        ToggleAudioUseCase(audioSettings)
    }
    override val recordGameFinishedUseCase: RecordGameFinishedUseCase by lazy {
        RecordGameFinishedUseCase(coinAccount, runHistoryRepository)
    }
}
