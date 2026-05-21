package com.jn.paxl.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jn.paxl.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreRepository(private val context: Context) {
    private val COINS_KEY = intPreferencesKey("tokens")
    private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
    private val LEADERBOARD_KEY = stringPreferencesKey("leaderboard_entries")
    private val SOUND_ENABLED_KEY = booleanPreferencesKey("sound_enabled")
    private val MUSIC_ENABLED_KEY = booleanPreferencesKey("music_enabled")

    val coinsFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[COINS_KEY] ?: 100 // Starting tokens
    }

    val highScoreFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGH_SCORE_KEY] ?: 0
    }

    val leaderboardFlow: Flow<List<LeaderboardEntry>> = context.dataStore.data.map { preferences ->
        decodeLeaderboard(preferences[LEADERBOARD_KEY].orEmpty())
    }

    val soundEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SOUND_ENABLED_KEY] ?: true
    }

    val musicEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MUSIC_ENABLED_KEY] ?: true
    }

    suspend fun saveCoins(tokens: Int) {
        context.dataStore.edit { preferences ->
            preferences[COINS_KEY] = tokens
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

    suspend fun saveLeaderboardEntry(entry: LeaderboardEntry, maxEntries: Int = 100) {
        context.dataStore.edit { preferences ->
            val current = decodeLeaderboard(preferences[LEADERBOARD_KEY].orEmpty()).toMutableList()
            current.add(entry)
            preferences[LEADERBOARD_KEY] = encodeLeaderboard(current.takeLast(maxEntries))
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

    private fun encodeLeaderboard(entries: List<LeaderboardEntry>): String {
        return entries.joinToString(";") { "${it.score},${it.durationSeconds},${it.recordedAtEpochMs}" }
    }

    private fun decodeLeaderboard(raw: String): List<LeaderboardEntry> {
        if (raw.isBlank()) return emptyList()
        return raw.split(";").mapNotNull { row ->
            val parts = row.split(",")
            if (parts.size != 3) return@mapNotNull null

            val score = parts[0].toIntOrNull() ?: return@mapNotNull null
            val duration = parts[1].toLongOrNull() ?: return@mapNotNull null
            val recordedAt = parts[2].toLongOrNull() ?: return@mapNotNull null

            LeaderboardEntry(
                score = score,
                durationSeconds = duration,
                recordedAtEpochMs = recordedAt
            )
        }
    }
}
