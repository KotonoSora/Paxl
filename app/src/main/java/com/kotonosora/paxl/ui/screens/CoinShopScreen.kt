package com.kotonosora.paxl.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.kotonosora.paxl.ui.components.NeonButton
import com.kotonosora.paxl.ui.components.NeonText
import com.kotonosora.paxl.ui.components.PaxlBackHeader
import com.kotonosora.paxl.ui.components.PaxlScreenScaffold
import com.kotonosora.paxl.ui.theme.NeonCyan
import com.kotonosora.paxl.ui.theme.NeonYellow
import com.kotonosora.paxl.viewmodel.GameViewModel

@Composable
fun CoinShopScreen(viewModel: GameViewModel, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val products by viewModel.shopProducts.collectAsState()
    val context = LocalContext.current

    PaxlScreenScaffold {
        PaxlBackHeader(
            title = "SHOP",
            titleColor = NeonYellow,
            titleFontSize = 32,
            onBack = onBack,
            horizontalSpacing = 8.dp,
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Stars,
                        contentDescription = null,
                        tint = NeonYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    NeonText("${uiState.coins}", color = NeonYellow, fontSize = 20)
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonYellow)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(products) { product ->
                    NeonButton(
                        text = "${product.title} - ${product.price}",
                        color = NeonCyan,
                        onClick = {
                            (context as? Activity)?.let { activity ->
                                viewModel.purchaseCoins(activity, product)
                            }
                        }
                    )
                }
            }
        }
    }
}
