package com.jn.paxl.application.shop

import com.jn.paxl.domain.shop.ShopUiState
import com.jn.paxl.domain.shop.ShopProduct
import com.jn.paxl.repository.BillingStatus
import com.jn.paxl.repository.StoreProduct
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveShopUiStateUseCaseTest {

    private val useCase = ObserveShopUiStateUseCase()

    @Test
    fun `returns loading while products are still loading`() = runTest {
        val products = MutableStateFlow(emptyList<StoreProduct>())
        val status = MutableStateFlow(BillingStatus.CONNECTING)

        val state = useCase(products, status).first()

        assertEquals(ShopUiState.Loading, state)
    }

    @Test
    fun `returns empty when loading finished and no products exist`() = runTest {
        val products = MutableStateFlow(emptyList<StoreProduct>())
        val status = MutableStateFlow(BillingStatus.EMPTY)

        val state = useCase(products, status).first()

        assertEquals(ShopUiState.Empty, state)
    }

    @Test
    fun `returns ready with products when loading finished and products exist`() = runTest {
        val product = StoreProduct("tokens_100", 100, "100 Tokens", "$0.59")
        val products = MutableStateFlow(listOf(product))
        val status = MutableStateFlow(BillingStatus.CONNECTED)

        val state = useCase(products, status).first()

        assertTrue(state is ShopUiState.Ready)
        val readyState = state as ShopUiState.Ready
        assertEquals(
            listOf(ShopProduct(productId = "tokens_100", tokenAmount = 100, price = "$0.59")),
            readyState.products
        )
    }

    @Test
    fun `returns error when billing status is error`() = runTest {
        val products = MutableStateFlow(emptyList<StoreProduct>())
        val status = MutableStateFlow(BillingStatus.ERROR)

        val state = useCase(products, status).first()

        assertEquals(ShopUiState.Error, state)
    }
}

