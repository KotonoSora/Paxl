package local.paxl.shop.domain.service

import local.core.billing.DomainProduct
import local.paxl.shop.domain.model.TokenPackIds
import java.util.Locale

/**
 * Paxl token-pack catalog. Core [local.core.shop.coins.CoinPacks] is keyed by
 * `coins_*` IDs, so Paxl resolves its `tokens_*` IDs here (explicit map plus
 * a `tokens_` prefix fallback mirroring the core pattern) and passes the
 * result to [local.core.billing.BillingConfig] as `coinAmountFor`.
 */
object TokenPacks {
    private val amounts = mapOf(
        TokenPackIds.TOKENS_100 to 100,
        TokenPackIds.TOKENS_500 to 500,
        TokenPackIds.TOKENS_1000 to 1000,
        TokenPackIds.TOKENS_1500 to 1500,
        TokenPackIds.TOKENS_2000 to 2000,
        TokenPackIds.TOKENS_2500 to 2500,
        TokenPackIds.TOKENS_3000 to 3000,
        TokenPackIds.TOKENS_3500 to 3500,
        TokenPackIds.TOKENS_4000 to 4000
    )

    fun getTokenAmount(productId: String): Int? =
        amounts[productId] ?: productId.removePrefix("tokens_").toIntOrNull()

    fun formatTokenPackName(tokenAmount: Int): String =
        String.format(Locale.US, "%,d Tokens", tokenAmount)

    /** Debug prices carried over from the monolith's mock catalog. */
    val mockProducts: List<DomainProduct> = listOf(
        TokenPackIds.TOKENS_100 to "$0.59",
        TokenPackIds.TOKENS_500 to "$0.79",
        TokenPackIds.TOKENS_1000 to "$0.99",
        TokenPackIds.TOKENS_1500 to "$1.89",
        TokenPackIds.TOKENS_2000 to "$2.89",
        TokenPackIds.TOKENS_2500 to "$3.89",
        TokenPackIds.TOKENS_3000 to "$4.89",
        TokenPackIds.TOKENS_3500 to "$5.89",
        TokenPackIds.TOKENS_4000 to "$6.89"
    ).map { (productId, price) ->
        DomainProduct(
            productId = productId,
            name = formatTokenPackName(getTokenAmount(productId) ?: 0),
            formattedPrice = price
        )
    }
}
