package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "parsminda_prefs")

data class AppPreferences(
    val onboardingCompleted: Boolean = false,
    val soundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val lastSyncEpochMillis: Long = 0L,
    val lastSyncMessage: String = "آماده همگام‌سازی آفلاین"
)

class PreferencesManager(private val context: Context) {
    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LAST_SYNC_MILLIS = longPreferencesKey("last_sync_millis")
        val LAST_SYNC_MESSAGE = stringPreferencesKey("last_sync_message")
    }

    val preferencesFlow: Flow<AppPreferences> = context.dataStore.data.map { prefs ->
        AppPreferences(
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            lastSyncEpochMillis = prefs[Keys.LAST_SYNC_MILLIS] ?: 0L,
            lastSyncMessage = prefs[Keys.LAST_SYNC_MESSAGE] ?: "آماده همگام‌سازی آفلاین"
        )
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun recordSyncOutcome(timestampMillis: Long, message: String) {
        context.dataStore.edit {
            it[Keys.LAST_SYNC_MILLIS] = timestampMillis
            it[Keys.LAST_SYNC_MESSAGE] = message
        }
    }
}
