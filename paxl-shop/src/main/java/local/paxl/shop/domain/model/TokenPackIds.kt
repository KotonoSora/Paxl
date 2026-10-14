package local.paxl.shop.domain.model

/**
 * Coin-pack product IDs. Paxl keeps its `tokens_*` IDs (Play Console
 * products); amounts resolve via [local.paxl.shop.domain.service.TokenPacks].
 */
object TokenPackIds {
    const val TOKENS_100 = "tokens_100"
    const val TOKENS_500 = "tokens_500"
    const val TOKENS_1000 = "tokens_1000"
    const val TOKENS_1500 = "tokens_1500"
    const val TOKENS_2000 = "tokens_2000"
    const val TOKENS_2500 = "tokens_2500"
    const val TOKENS_3000 = "tokens_3000"
    const val TOKENS_3500 = "tokens_3500"
    const val TOKENS_4000 = "tokens_4000"

    val ALL = listOf(
        TOKENS_100,
        TOKENS_500,
        TOKENS_1000,
        TOKENS_1500,
        TOKENS_2000,
        TOKENS_2500,
        TOKENS_3000,
        TOKENS_3500,
        TOKENS_4000
    )
}
