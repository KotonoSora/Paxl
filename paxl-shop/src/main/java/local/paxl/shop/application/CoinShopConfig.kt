package local.paxl.shop.application

import local.core.billing.DomainProduct
import local.core.shop.coins.CoinPackItem
import local.core.shop.coins.CoinPacks
import local.paxl.shop.domain.service.TokenPacks
import local.core.shop.coins.R as ShopR

/**
 * Per-game coin-shop copy. Paxl's `tokens_*` IDs have no core definition, so
 * the display mapping is built here from [TokenPacks] amounts with the game's
 * explicit description template — core keeps only its fallback template,
 * never a per-game global.
 */
object CoinShopConfig {
    const val DESCRIPTION_TEMPLATE =
        "A pack of %d tokens used for Undo, Shuffle, and Continue."

    fun coinPackItem(product: DomainProduct): CoinPackItem {
        val amount = TokenPacks.getTokenAmount(product.productId) ?: 0
        return CoinPackItem(
            id = product.productId,
            name = TokenPacks.formatTokenPackName(amount),
            description = CoinPacks.formatDescription(amount, DESCRIPTION_TEMPLATE),
            price = product.formattedPrice,
            coinAmount = amount,
            imageRes = imageResFor(amount)
        )
    }

    // Mirrors core's drawable buckets; drawables are owned by :core-shop-coins
    // and referenced through its R.
    private fun imageResFor(coinAmount: Int): Int = when {
        coinAmount <= 100 -> ShopR.drawable.img_coins_100
        coinAmount <= 500 -> ShopR.drawable.img_coins_500
        coinAmount <= 1000 -> ShopR.drawable.img_coins_1000
        coinAmount <= 1500 -> ShopR.drawable.img_coins_1000
        coinAmount <= 2000 -> ShopR.drawable.img_coins_2000
        else -> ShopR.drawable.img_coins_2000
    }
}
