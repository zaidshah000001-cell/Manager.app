package com.zaidshah.manager.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class AppPreferences(private val context: Context) {
    companion object {
        private val KEY_PROFILE_NAME = stringPreferencesKey("profile_name")
        private val KEY_AVATAR_ID = intPreferencesKey("avatar_id")
        private val KEY_THEME_PRESET = stringPreferencesKey("theme_preset")
        private val KEY_PRIMARY_COLOR = stringPreferencesKey("custom_primary_hex")
        private val KEY_BACKGROUND_COLOR = stringPreferencesKey("custom_bg_hex")
        private val KEY_RECENT_COLORS = stringPreferencesKey("recent_accent_colors")
    }

    val profileName: Flow<String> = context.dataStore.data.map { it[KEY_PROFILE_NAME] ?: "Zaid" }
    val avatarId: Flow<Int> = context.dataStore.data.map { it[KEY_AVATAR_ID] ?: 0 }
    val themePreset: Flow<String> = context.dataStore.data.map { it[KEY_THEME_PRESET] ?: "FOREST" }
    val customPrimary: Flow<String?> = context.dataStore.data.map { it[KEY_PRIMARY_COLOR] }
    val customBackground: Flow<String?> = context.dataStore.data.map { it[KEY_BACKGROUND_COLOR] }
    val recentColors: Flow<List<String>> = context.dataStore.data.map { data ->
        data[KEY_RECENT_COLORS]?.split(",")?.filter { it.matches(Regex("#[0-9A-Fa-f]{6}")) } ?: emptyList()
    }

    suspend fun applyAccent(hex: String) {
        require(hex.matches(Regex("#[0-9A-Fa-f]{6}")))
        context.dataStore.edit { data ->
            data[KEY_PRIMARY_COLOR] = hex.uppercase()
            data[KEY_THEME_PRESET] = "CUSTOM"
            val history = data[KEY_RECENT_COLORS]?.split(",") ?: emptyList()
            data[KEY_RECENT_COLORS] = (listOf(hex.uppercase()) + history.filterNot { it.equals(hex, true) }).take(20).joinToString(",")
        }
    }

    suspend fun setProfileName(name: String) {
        context.dataStore.edit { it[KEY_PROFILE_NAME] = name }
    }

    suspend fun setAvatarId(id: Int) {
        context.dataStore.edit { it[KEY_AVATAR_ID] = id }
    }

    suspend fun setThemePreset(preset: String) {
        context.dataStore.edit { it[KEY_THEME_PRESET] = preset }
    }

    suspend fun setCustomColors(primaryHex: String, bgHex: String) {
        context.dataStore.edit {
            it[KEY_PRIMARY_COLOR] = primaryHex
            it[KEY_BACKGROUND_COLOR] = bgHex
            it[KEY_THEME_PRESET] = "CUSTOM"
        }
    }
}
