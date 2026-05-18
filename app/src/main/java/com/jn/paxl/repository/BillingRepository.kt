package com.jn.paxl.repository

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoreProduct(
    val productId: String,
    val title: String,
    val price: String,
    val originalDetails: ProductDetails? = null
)

class BillingRepository(
    private val context: Context,
    private val dataStoreRepository: DataStoreRepository
) : PurchasesUpdatedListener {

    private val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enableOneTimeProducts()
        .build()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(pendingPurchasesParams)
        .build()

    private val _products = MutableStateFlow<List<StoreProduct>>(emptyList())
    val products: StateFlow<List<StoreProduct>> = _products.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        if (isDebug) {
            queryProducts()
        } else {
            startConnection()
        }
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Connection lost. Retrying...
            }
        })
    }

    private fun queryProducts() {
        if (isDebug) {
            val mockProducts = listOf(
                StoreProduct("tokens_100", "100 Coins", "$0.59"),
                StoreProduct("tokens_500", "500 Coins", "$0.79"),
                StoreProduct("tokens_1000", "1000 Coins", "$0.99"),
                StoreProduct("tokens_1500", "1500 Coins", "$1.89"),
                StoreProduct("tokens_2000", "2000 Coins", "$2.89"),
                StoreProduct("tokens_2500", "2500 Coins", "$3.89"),
                StoreProduct("tokens_3000", "3000 Coins", "$4.89"),
                StoreProduct("tokens_3500", "3500 Coins", "$5.89"),
                StoreProduct("tokens_4000", "4000 Coins", "$6.89")
            )
            _products.value = mockProducts
            return
        }

        val productIds = listOf(
            "tokens_100", "tokens_500", "tokens_1000", "tokens_1500",
            "tokens_2000", "tokens_2500", "tokens_3000", "tokens_3500", "tokens_4000"
        )

        val productList = productIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val sortedProducts = result.productDetailsList.sortedBy { product ->
                    getCoinAmountFromId(product.productId)
                }.map {
                    StoreProduct(
                        productId = it.productId,
                        title = it.title,
                        price = it.oneTimePurchaseOfferDetails?.formattedPrice ?: "Unknown",
                        originalDetails = it
                    )
                }
                _products.value = sortedProducts
            }
        }
    }

    fun launchBillingFlow(activity: Activity, product: StoreProduct) {
        if (isDebug && product.originalDetails == null) {
            grantCoins(listOf(product.productId))
            return
        }

        val originalDetails = product.originalDetails ?: return

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(originalDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Consuming the purchase also acknowledges it and allows it to be bought again (consumable)
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.consumeAsync(consumeParams) { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    grantCoins(purchase.products)
                }
            }
        }
    }

    private fun grantCoins(productIds: List<String>) {
        scope.launch {
            var coinsToAdd = 0
            for (productId in productIds) {
                coinsToAdd += getCoinAmountFromId(productId)
            }
            if (coinsToAdd > 0) {
                dataStoreRepository.updateCoins(coinsToAdd)
            }
        }
    }

    private fun getCoinAmountFromId(productId: String): Int {
        return when (productId) {
            "tokens_100" -> 100
            "tokens_500" -> 500
            "tokens_1000" -> 1000
            "tokens_1500" -> 1500
            "tokens_2000" -> 2000
            "tokens_2500" -> 2500
            "tokens_3000" -> 3000
            "tokens_3500" -> 3500
            "tokens_4000" -> 4000
            else -> 0
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}
