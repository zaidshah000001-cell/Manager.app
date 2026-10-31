package com.zaidshah.manager.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaidshah.manager.data.preferences.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val profileName: String = "Zaid",
    val avatarId: Int = 0,
    val themePreset: String = "FOREST",
    val customPrimary: String? = null,
    val customBackground: String? = null,
    val recentColors: List<String> = emptyList()
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = AppPreferences(application)

    val uiState: StateFlow<SettingsUiState> = combine(
        prefs.profileName,
        prefs.avatarId,
        prefs.themePreset,
        prefs.customPrimary,
        prefs.customBackground,
        prefs.recentColors
    ) { values ->
        val name = values[0] as String
        val avatar = values[1] as Int
        val theme = values[2] as String
        val prim = values[3] as String?
        val bg = values[4] as String?
        @Suppress("UNCHECKED_CAST") val recent = values[5] as List<String>
        SettingsUiState(
            profileName = name,
            avatarId = avatar,
            themePreset = theme,
            customPrimary = prim,
            customBackground = bg,
            recentColors = recent
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun updateProfileName(name: String) {
        viewModelScope.launch { prefs.setProfileName(name) }
    }

    fun updateAvatarId(id: Int) {
        viewModelScope.launch { prefs.setAvatarId(id) }
    }

    fun setThemePreset(preset: String) {
        viewModelScope.launch { prefs.setThemePreset(preset) }
    }

    fun applyAccent(hex: String) {
        viewModelScope.launch { prefs.applyAccent(hex) }
    }

    fun setCustomColors(primaryHex: String, bgHex: String) {
        viewModelScope.launch { prefs.setCustomColors(primaryHex, bgHex) }
    }
}
