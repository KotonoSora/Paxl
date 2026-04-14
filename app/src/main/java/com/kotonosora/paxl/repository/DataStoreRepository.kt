package com.kotonosora.paxl.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreRepository(private val context: Context) {
    private val COINS_KEY = intPreferencesKey("coins")
    private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
    private val SOUND_ENABLED_KEY = booleanPreferencesKey("sound_enabled")
    private val MUSIC_ENABLED_KEY = booleanPreferencesKey("music_enabled")

    val coinsFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[COINS_KEY] ?: 100 // Starting coins
    }

    val highScoreFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGH_SCORE_KEY] ?: 0
    }

    val soundEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SOUND_ENABLED_KEY] ?: true
    }

    val musicEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MUSIC_ENABLED_KEY] ?: true
    }

    suspend fun saveCoins(coins: Int) {
        context.dataStore.edit { preferences ->
            preferences[COINS_KEY] = coins
        }
    }

    suspend fun updateCoins(amount: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: 100
            preferences[COINS_KEY] = current + amount
        }
    }

    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHigh = preferences[HIGH_SCORE_KEY] ?: 0
            if (score > currentHigh) {
                preferences[HIGH_SCORE_KEY] = score
            }
        }
    }

    suspend fun saveSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED_KEY] = enabled
        }
    }

    suspend fun saveMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MUSIC_ENABLED_KEY] = enabled
        }
    }
}
