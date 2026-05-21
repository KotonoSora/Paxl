package com.jn.paxl.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.paxl.model.GameUiState
import com.jn.paxl.repository.StoreProduct
import com.jn.paxl.ui.LocalSoundManager
import com.jn.paxl.ui.components.GameBackHeader
import com.jn.paxl.ui.components.GameScreenScaffold
import com.jn.paxl.ui.components.NeonButton
import com.jn.paxl.ui.components.NeonText
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.NeonYellow
import com.jn.paxl.viewmodel.GameViewModel

@Composable
fun CoinShopScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val products by viewModel.shopProducts.collectAsState()
    val context = LocalContext.current

    CoinShopScreenContent(
        uiState = uiState, products = products, onBack = onBack, onPurchase = { product ->
            (context as? Activity)?.let { activity ->
                viewModel.purchaseCoins(activity, product)
            }
        })
}

@Composable
fun CoinShopScreenContent(
    uiState: GameUiState,
    products: List<StoreProduct>,
    onBack: () -> Unit,
    onPurchase: (StoreProduct) -> Unit
) {
    val soundManager = LocalSoundManager.current

    GameScreenScaffold {
        GameBackHeader(title = "SHOP", titleColor = NeonYellow, titleFontSize = 24, onBack = {
            soundManager?.playClick()
            onBack()
        }, horizontalSpacing = 8.dp, trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    tint = NeonYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                NeonText("${uiState.tokens}", color = NeonYellow, fontSize = 20)
            }
        })

        if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonYellow)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    NeonButton(
                        text = "${product.title} - ${product.price}", color = NeonCyan, onClick = {
                            soundManager?.playClick()
                            onPurchase(product)
                        })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoinShopScreenPreview() {
    GameTheme {
        CoinShopScreenContent(
            uiState = GameUiState(tokens = 500),
            products = listOf(
                StoreProduct("1", "100 COINS", "0.99$"),
                StoreProduct("2", "500 COINS", "3.99$"),
            ),
            onBack = {},
            onPurchase = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyCoinShopScreenPreview() {
    GameTheme {
        CoinShopScreenContent(
            uiState = GameUiState(tokens = 500),
            products = listOf(),
            onBack = {},
            onPurchase = {})
    }
}
