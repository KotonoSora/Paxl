package com.jn.paxl.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jn.paxl.domain.shop.ShopProduct
import com.jn.paxl.domain.shop.ShopUiState
import com.jn.paxl.model.GameUiState
import com.jn.paxl.ui.LocalSoundManager
import com.jn.paxl.ui.components.GameBackHeader
import com.jn.paxl.ui.components.NeonButton
import com.jn.paxl.ui.components.GameScreenScaffold
import com.jn.paxl.ui.components.NeonText
import com.jn.paxl.ui.components.RetroFont
import com.jn.paxl.ui.theme.GameTheme
import com.jn.paxl.ui.theme.NeonCyan
import com.jn.paxl.ui.theme.NeonYellow
import com.jn.paxl.viewmodel.GameViewModel

@Composable
fun CoinShopScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val shopUiState by viewModel.shopUiState.collectAsState()
    val context = LocalContext.current

    CoinShopScreenContent(
        uiState = uiState,
        shopUiState = shopUiState,
        onBack = onBack,
        onRefresh = { viewModel.refreshShopProducts() },
        onPurchase = { product ->
            (context as? Activity)?.let { activity ->
                viewModel.purchaseCoinsByProductId(activity, product.productId)
            }
        }
    )
}

@Composable
fun CoinShopScreenContent(
    uiState: GameUiState,
    shopUiState: ShopUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onPurchase: (ShopProduct) -> Unit
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

        when (shopUiState) {
            ShopUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonYellow)
                }
            }

            ShopUiState.Error -> {
                EmptyCoinShopState(
                    title = "SHOP TEMPORARILY UNAVAILABLE",
                    message = "Unable to connect to billing service.\nPlease try again.",
                    actionText = "TRY AGAIN",
                    onRefresh = {
                        soundManager?.playClick()
                        onRefresh()
                    }
                )
            }

            ShopUiState.Empty -> {
                EmptyCoinShopState(
                    onRefresh = {
                        soundManager?.playClick()
                        onRefresh()
                    }
                )
            }

            is ShopUiState.Ready -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(shopUiState.products, key = { it.productId }) { product ->
                        CoinShopItemRow(product = product, onClick = {
                            soundManager?.playClick()
                            onPurchase(product)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCoinShopState(
    onRefresh: () -> Unit,
    title: String = "NO PACKS AVAILABLE",
    message: String = "Unable to load coin packs.\nPlease check your connection and retry.",
    actionText: String = "RETRY"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Stars,
            contentDescription = null,
            tint = NeonYellow.copy(alpha = 0.8f),
            modifier = Modifier.size(42.dp)
        )
        Spacer(Modifier.height(12.dp))
        NeonText(text = title, color = NeonYellow, fontSize = 16, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        NeonText(
            text = message,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 10,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        NeonButton(
            text = actionText,
            color = NeonCyan,
            modifier = Modifier.fillMaxWidth(),
            onClick = onRefresh
        )
    }
}

@Composable
private fun CoinShopItemRow(product: ShopProduct, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    val tokenValue = "${product.tokenAmount}"
    val tokenSize = adaptiveTokenFontSize(tokenValue)
    val priceSize = adaptivePriceFontSize(product.price)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.78f),
                        Color.Black.copy(alpha = 0.95f)
                    )
                )
            )
            .border(2.dp, NeonCyan, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            NeonText(text = "TOKENS", color = Color.White.copy(alpha = 0.75f), fontSize = 10)
            Text(
                text = tokenValue,
                color = NeonYellow,
                fontFamily = RetroFont,
                fontWeight = FontWeight.Bold,
                fontSize = tokenSize.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .height(44.dp)
                .width(1.dp)
                .background(Color.White.copy(alpha = 0.22f))
        )

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            NeonText(text = "PRICE", color = Color.White.copy(alpha = 0.75f), fontSize = 10)
            Text(
                text = product.price,
                color = NeonCyan,
                fontFamily = RetroFont,
                fontWeight = FontWeight.Bold,
                fontSize = priceSize.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
        }
    }
}


private fun adaptiveTokenFontSize(value: String): Int {
    return when {
        value.length > 24 -> 12
        value.length > 18 -> 13
        value.length > 12 -> 15
        else -> 17
    }
}

private fun adaptivePriceFontSize(value: String): Int {
    return when {
        value.length > 20 -> 12
        value.length > 16 -> 13
        value.length > 12 -> 15
        else -> 17
    }
}

@Preview(showBackground = true)
@Composable
fun CoinShopScreenPreview() {
    GameTheme {
        CoinShopScreenContent(
            uiState = GameUiState(tokens = 500),
            shopUiState = ShopUiState.Ready(
                products = listOf(
                ShopProduct("tokens_100", 100, "$0.59"),
                ShopProduct("tokens_500", 500, "$0.79"),
                ShopProduct("tokens_1000", 1000, "$0.99"),
                ShopProduct("tokens_1500", 1500, "$1.89"),
                ShopProduct("tokens_2000", 2000, "$2.89"),
                ShopProduct("tokens_2500", 2500, "$3.89"),
                ShopProduct("tokens_3000", 3000, "$4.89"),
                ShopProduct("tokens_3500", 3500, "$5.89"),
                ShopProduct("tokens_4000", 4000, "$6.89"),
                )
            ),
            onBack = {},
            onRefresh = {},
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
            shopUiState = ShopUiState.Empty,
            onBack = {},
            onRefresh = {},
            onPurchase = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingCoinShopScreenPreview() {
    GameTheme {
        CoinShopScreenContent(
            uiState = GameUiState(tokens = 500),
            shopUiState = ShopUiState.Loading,
            onBack = {},
            onRefresh = {},
            onPurchase = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorCoinShopScreenPreview() {
    GameTheme {
        CoinShopScreenContent(
            uiState = GameUiState(tokens = 500),
            shopUiState = ShopUiState.Error,
            onBack = {},
            onRefresh = {},
            onPurchase = {}
        )
    }
}

