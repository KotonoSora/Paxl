package local.paxl.shop.infrastructure.persistence

import androidx.datastore.preferences.core.Preferences
import local.core.ui.data.SettingsRepositoryImpl

/**
 * Game-owned inventory map, passed into core [SettingsRepositoryImpl].
 * Paxl is coin-direct (no inventoried power-ups), so the map is empty;
 * the frozen inventory keys stay in core untouched for schema stability.
 */
object PaxlInventoryKeys {
    fun map(): Map<String, Preferences.Key<Int>> = SettingsRepositoryImpl.inventoryKeysFor()
}
