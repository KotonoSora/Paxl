package com.jn.paxl.application.shop

import com.jn.paxl.domain.shop.ShopUiState
import com.jn.paxl.domain.shop.ShopProduct
import com.jn.paxl.repository.BillingStatus
import com.jn.paxl.repository.StoreProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveShopUiStateUseCase {
    operator fun invoke(
        productsFlow: Flow<List<StoreProduct>>,
        billingStatusFlow: Flow<BillingStatus>
    ): Flow<ShopUiState> {
        return combine(productsFlow, billingStatusFlow) { products, billingStatus ->
            when (billingStatus) {
                BillingStatus.IDLE,
                BillingStatus.CONNECTING -> ShopUiState.Loading

                BillingStatus.ERROR -> ShopUiState.Error
                BillingStatus.EMPTY -> ShopUiState.Empty
                BillingStatus.CONNECTED -> if (products.isEmpty()) {
                    ShopUiState.Empty
                } else {
                    ShopUiState.Ready(products.map { it.toDomainShopProduct() })
                }
            }
        }
    }

    private fun StoreProduct.toDomainShopProduct(): ShopProduct {
        return ShopProduct(
            productId = productId,
            tokenAmount = tokenAmount,
            price = price
        )
    }
}

