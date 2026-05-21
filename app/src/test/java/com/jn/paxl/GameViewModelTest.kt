package com.jn.paxl

import androidx.compose.ui.graphics.Color
import com.jn.paxl.application.gameplay.GameplayUseCases
import com.jn.paxl.application.gameplay.PlaceBlockUseCase
import com.jn.paxl.application.gameplay.ReshuffleBlocksUseCase
import com.jn.paxl.application.gameplay.StartNewGameUseCase
import com.jn.paxl.application.gameplay.UndoMoveUseCase
import com.jn.paxl.application.shop.ObserveShopUiStateUseCase
import com.jn.paxl.application.shop.ShopUseCases
import com.jn.paxl.domain.gameplay.port.BlockCatalog
import com.jn.paxl.repository.BillingStatus
import com.jn.paxl.model.Block
import com.jn.paxl.model.Coordinate
import com.jn.paxl.repository.BillingRepository
import com.jn.paxl.repository.DataStoreRepository
import com.jn.paxl.viewmodel.GameViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GameViewModel
    private val dataStoreRepository = mockk<DataStoreRepository>(relaxed = true)
    private val billingRepository = mockk<BillingRepository>(relaxed = true)

    private val singleCellBlock = Block(
        shape = listOf(Coordinate(0, 0)),
        color = Color.Blue,
        id = "single"
    )

    private val fakeCatalog = object : BlockCatalog {
        private var batch = 0

        override fun randomBlocks(count: Int): List<Block> {
            val currentBatch = batch++
            return List(count) { singleCellBlock.copy(id = "b${currentBatch}-$it") }
        }
    }

    private val gameplayUseCases = GameplayUseCases(
        startNewGame = StartNewGameUseCase(fakeCatalog),
        placeBlock = PlaceBlockUseCase(fakeCatalog),
        undoMove = UndoMoveUseCase(),
        reshuffleBlocks = ReshuffleBlocksUseCase(fakeCatalog)
    )

    private val shopUseCases = ShopUseCases(
        observeShopUiState = ObserveShopUiStateUseCase()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock flows
        every { dataStoreRepository.coinsFlow } returns flowOf(100)
        every { dataStoreRepository.highScoreFlow } returns flowOf(0)
        every { dataStoreRepository.leaderboardFlow } returns flowOf(emptyList())
        every { dataStoreRepository.soundEnabledFlow } returns flowOf(true)
        every { dataStoreRepository.musicEnabledFlow } returns flowOf(true)
        every { billingRepository.products } returns MutableStateFlow(emptyList())
        every { billingRepository.billingStatus } returns MutableStateFlow(BillingStatus.CONNECTED)

        viewModel = GameViewModel(
            dataStoreRepository,
            billingRepository,
            gameplayUseCases,
            shopUseCases
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0, state.score)
        assertEquals(3, state.availableBlocks.size)
        assertFalse(state.isGameOver)
        assertEquals(10, state.grid.size)
    }

    @Test
    fun `placing a block updates score and grid`() = runTest {
        advanceUntilIdle()
        val firstBlock = viewModel.uiState.value.availableBlocks.first()

        viewModel.onBlockPlaced(firstBlock, Coordinate(0, 0))

        val state = viewModel.uiState.value
        assertTrue(state.score > 0)
        assertNotNull(state.grid.cells[Coordinate(0, 0)])
        assertEquals(2, state.availableBlocks.size)

        advanceUntilIdle()
        coVerify(exactly = 1) { dataStoreRepository.saveHighScore(state.score) }
    }

    @Test
    fun `clearing a line increases score and clears cells`() = runTest {
        advanceUntilIdle()
        viewModel.startNewGame()

        val singleBlock = Block(listOf(Coordinate(0, 0)), Color.Blue)

        for (x in 0 until 9) {
            viewModel.onBlockPlaced(singleBlock, Coordinate(x, 0))
        }

        val scoreBeforeClear = viewModel.uiState.value.score

        viewModel.onBlockPlaced(singleBlock, Coordinate(9, 0))

        val state = viewModel.uiState.value
        assertEquals(scoreBeforeClear + 110, state.score)
        assertTrue(state.isClearing)
        for (x in 0 until 10) {
            assertNull(state.grid.cells[Coordinate(x, 0)])
        }

        advanceTimeBy(1000)
        advanceUntilIdle()

        val clearedState = viewModel.uiState.value
        assertFalse(clearedState.isClearing)

        assertTrue(clearedState.clearingCells.isEmpty())
    }


    @Test
    fun `undo move restores previous grid state`() = runTest {
        advanceUntilIdle()
        val firstBlock = viewModel.uiState.value.availableBlocks.first()

        viewModel.onBlockPlaced(firstBlock, Coordinate(0, 0))
        assertNotNull(viewModel.uiState.value.grid.cells[Coordinate(0, 0)])

        viewModel.undoMove()

        assertNull(viewModel.uiState.value.grid.cells[Coordinate(0, 0)])
        assertEquals(90, viewModel.uiState.value.tokens)

        advanceUntilIdle()
        coVerify(exactly = 1) { dataStoreRepository.saveCoins(90) }
    }

    @Test
    fun `undo move does nothing when history is empty`() = runTest {
        advanceUntilIdle()

        val before = viewModel.uiState.value
        viewModel.undoMove()
        val after = viewModel.uiState.value

        assertEquals(before.grid.cells, after.grid.cells)
        assertEquals(before.tokens, after.tokens)

        advanceUntilIdle()
        coVerify(exactly = 0) { dataStoreRepository.saveCoins(any()) }
    }

    @Test
    fun `reshuffle deducts tokens and saves when balance is sufficient`() = runTest {
        advanceUntilIdle()

        val beforeIds = viewModel.uiState.value.availableBlocks.map { it.id }
        viewModel.reshuffleBlocks()
        val after = viewModel.uiState.value

        assertEquals(75, after.tokens)
        assertEquals(3, after.availableBlocks.size)
        assertTrue(after.availableBlocks.map { it.id } != beforeIds)

        advanceUntilIdle()
        coVerify(exactly = 1) { dataStoreRepository.saveCoins(75) }
    }

    @Test
    fun `start new game clears undo history`() = runTest {
        advanceUntilIdle()
        val firstBlock = viewModel.uiState.value.availableBlocks.first()

        viewModel.onBlockPlaced(firstBlock, Coordinate(0, 0))
        assertNotNull(viewModel.uiState.value.grid.cells[Coordinate(0, 0)])

        viewModel.startNewGame()
        val afterStart = viewModel.uiState.value
        assertTrue(afterStart.grid.cells.isEmpty())

        viewModel.undoMove()
        val afterUndo = viewModel.uiState.value
        assertTrue(afterUndo.grid.cells.isEmpty())
        assertEquals(afterStart.tokens, afterUndo.tokens)
    }

    @Test
    fun `continue play keeps score and timer but resets board and blocks`() = runTest {
        advanceUntilIdle()

        val beforeContinue = viewModel.uiState.value.copy(
            grid = viewModel.uiState.value.grid.copy(cells = mapOf(Coordinate(0, 0) to Color.Red)),
            score = 840,
            tokens = 100,
            isGameOver = true,
            isWin = false,
            sessionStartMs = 12345L,
            availableBlocks = listOf(singleCellBlock)
        )

        val field = GameViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<com.jn.paxl.model.GameUiState>
        stateFlow.value = beforeContinue

        viewModel.continuePlayAfterGameOver()

        val continued = viewModel.uiState.value
        assertFalse(continued.isGameOver)
        assertFalse(continued.isWin)
        assertEquals(840, continued.score)
        assertEquals(70, continued.tokens)
        assertEquals(12345L, continued.sessionStartMs)
        assertTrue(continued.grid.cells.isEmpty())
        assertEquals(3, continued.availableBlocks.size)

        advanceUntilIdle()
        coVerify(exactly = 1) { dataStoreRepository.saveCoins(70) }
    }

    @Test
    fun `continue play does nothing when tokens are insufficient`() = runTest {
        advanceUntilIdle()

        val beforeContinue = viewModel.uiState.value.copy(
            grid = viewModel.uiState.value.grid.copy(cells = mapOf(Coordinate(0, 0) to Color.Red)),
            score = 500,
            tokens = 20,
            isGameOver = true,
            isWin = false,
            sessionStartMs = 5678L,
            availableBlocks = listOf(singleCellBlock)
        )

        val field = GameViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<com.jn.paxl.model.GameUiState>
        stateFlow.value = beforeContinue

        viewModel.continuePlayAfterGameOver()

        val after = viewModel.uiState.value
        assertEquals(beforeContinue, after)

        advanceUntilIdle()
        coVerify(exactly = 0) { dataStoreRepository.saveCoins(any()) }
    }

    @Test
    fun `continue after win keeps current board and skips win condition`() = runTest {
        advanceUntilIdle()

        val currentGrid = mapOf(Coordinate(0, 0) to Color.Red, Coordinate(1, 1) to Color.Green)
        val beforeContinue = viewModel.uiState.value.copy(
            grid = viewModel.uiState.value.grid.copy(cells = currentGrid),
            score = 5100,
            tokens = 130,
            isGameOver = true,
            isWin = true,
            isWinConditionSkipped = false,
            availableBlocks = listOf(singleCellBlock)
        )

        val field = GameViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<com.jn.paxl.model.GameUiState>
        stateFlow.value = beforeContinue

        viewModel.continuePlayAfterGameOver()

        val after = viewModel.uiState.value
        assertFalse(after.isGameOver)
        assertFalse(after.isWin)
        assertTrue(after.isWinConditionSkipped)
        assertEquals(beforeContinue.score, after.score)
        assertEquals(beforeContinue.tokens, after.tokens)
        assertEquals(currentGrid, after.grid.cells)
        assertEquals(beforeContinue.availableBlocks, after.availableBlocks)

        advanceUntilIdle()
        coVerify(exactly = 0) { dataStoreRepository.saveCoins(any()) }
    }
}
