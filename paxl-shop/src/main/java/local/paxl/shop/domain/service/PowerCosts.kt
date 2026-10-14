package local.paxl.shop.domain.service

/**
 * Coin-direct gameplay costs (no power-up shop, no inventory): each use
 * spends tokens at use-time via core `CoinAccount.spendCoins`.
 */
object PowerCosts {
    const val UNDO_COST = 10
    const val RESHUFFLE_COST = 25
    const val CONTINUE_COST = 30

    /** Cheapest spend; gameplay shows the broke affordance below this. */
    const val CHEAPEST_COST = UNDO_COST
}
