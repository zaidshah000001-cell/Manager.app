package com.zaidshah.manager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class CustomThemeColors(
    val primary: Color,
    val background: Color,
    val cardBackground: Color = CardWhite,
    val track: Color = SageTrack,
    val textPrimary: Color = DarkText,
    val textMuted: Color = MutedText
)

@Composable
fun ManagerTheme(
    preset: String = "FOREST",
    customPrimary: Color? = null,
    customBg: Color? = null,
    content: @Composable () -> Unit
) {
    val accent = when (preset) {
        "NAVY_CREAM", "IVORY_NAVY" -> Color(0xFF003D74)
        "BURGUNDY" -> Color(0xFF800020)
        "PURPLE" -> Color(0xFF673AB7)
        "CUSTOM" -> customPrimary ?: ForestGreen
        else -> ForestGreen
    }

    val colors = CustomThemeColors(
        primary = accent,
        background = WarmCream,
        cardBackground = CardWhite,
        track = accent.copy(alpha = 0.12f),
        textPrimary = accent,
        textMuted = MutedText
    )

    val colorScheme = lightColorScheme(
        primary = colors.primary,
        background = colors.background,
        surface = colors.cardBackground,
        onPrimary = Color.White,
        onBackground = colors.textPrimary,
        onSurface = colors.textPrimary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
