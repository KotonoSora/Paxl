package com.jn.paxl.domain.shop

sealed interface ShopUiState {
    data object Loading : ShopUiState
    data object Error : ShopUiState
    data object Empty : ShopUiState
    data class Ready(val products: List<ShopProduct>) : ShopUiState
}

