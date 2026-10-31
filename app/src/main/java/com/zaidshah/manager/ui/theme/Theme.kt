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
    val colors = when (preset) {
        "IVORY_NAVY" -> CustomThemeColors(
            primary = Color(0xFF003D74),
            background = Color(0xFFFFFFF0),
            cardBackground = Color(0xFFFFFFFF),
            track = Color(0xFFE3EDF4),
            textPrimary = Color(0xFF003D74),
            textMuted = Color(0xFF536F86)
        )
        "MIDNIGHT" -> CustomThemeColors(
            primary = MidnightPrimary,
            background = MidnightBg,
            cardBackground = Color(0xFF1E293B),
            track = Color(0xFF334155),
            textPrimary = Color(0xFFF8FAFC),
            textMuted = Color(0xFF94A3B8)
        )
        "OCEAN" -> CustomThemeColors(
            primary = OceanPrimary,
            background = OceanBg,
            track = Color(0xFFE0F2FE),
            textPrimary = Color(0xFF0C4A6E),
            textMuted = Color(0xFF0369A1)
        )
        "CUSTOM" -> CustomThemeColors(
            primary = customPrimary ?: ForestGreen,
            background = customBg ?: WarmCream
        )
        else -> CustomThemeColors(
            primary = ForestGreen,
            background = WarmCream
        )
    }

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
