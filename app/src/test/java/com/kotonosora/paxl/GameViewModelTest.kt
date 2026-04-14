package com.kotonosora.paxl

import androidx.compose.ui.graphics.Color
import com.kotonosora.paxl.model.Block
import com.kotonosora.paxl.model.Coordinate
import com.kotonosora.paxl.repository.BillingRepository
import com.kotonosora.paxl.repository.DataStoreRepository
import com.kotonosora.paxl.viewmodel.GameViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GameViewModel
    private val dataStoreRepository = mockk<DataStoreRepository>(relaxed = true)
    private val billingRepository = mockk<BillingRepository>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock flows
        every { dataStoreRepository.coinsFlow } returns flowOf(100)
        every { dataStoreRepository.highScoreFlow } returns flowOf(0)
        every { dataStoreRepository.soundEnabledFlow } returns flowOf(true)
        every { dataStoreRepository.musicEnabledFlow } returns flowOf(true)

        viewModel = GameViewModel(dataStoreRepository, billingRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Run any pending coroutines
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
    }

    @Test
    fun `clearing a line increases score and clears cells`() = runTest {
        advanceUntilIdle()
        viewModel.startNewGame()

        // A simple 1x1 block for testing
        val singleBlock = Block(listOf(Coordinate(0, 0)), Color.Blue)

        // Place 9 blocks in row 0
        for (x in 0 until 9) {
            viewModel.onBlockPlaced(singleBlock, Coordinate(x, 0))
        }

        val scoreBeforeClear = viewModel.uiState.value.score

        // Place the 10th block to clear the row
        viewModel.onBlockPlaced(singleBlock, Coordinate(9, 0))

        val state = viewModel.uiState.value
        // 10 points for block placement + 100 for line clear
        assertEquals(scoreBeforeClear + 110, state.score)

        // Grid cells for that row should be empty
        for (x in 0 until 10) {
            assertNull(state.grid.cells[Coordinate(x, 0)])
        }
    }

    @Test
    fun `invalid placement does not update grid`() = runTest {
        advanceUntilIdle()
        val firstBlock = viewModel.uiState.value.availableBlocks.first()

        // Place once
        viewModel.onBlockPlaced(firstBlock, Coordinate(0, 0))
        val scoreAfterFirst = viewModel.uiState.value.score

        // Try to place on same spot
        viewModel.onBlockPlaced(firstBlock, Coordinate(0, 0))

        assertEquals(scoreAfterFirst, viewModel.uiState.value.score)
    }

    @Test
    fun `undo move restores previous grid state`() = runTest {
        advanceUntilIdle()
        val firstBlock = viewModel.uiState.value.availableBlocks.first()

        // Set coins for undo
        // Note: In a real test we might want to verify coins decrease too

        viewModel.onBlockPlaced(firstBlock, Coordinate(0, 0))
        assertNotNull(viewModel.uiState.value.grid.cells[Coordinate(0, 0)])

        viewModel.undoMove()
        // Assuming 100 coins initial, undo costs 10.
        // If coins are not enough, it won't undo. Initial state mocks 100 coins.

        assertNull(viewModel.uiState.value.grid.cells[Coordinate(0, 0)])
    }
}
