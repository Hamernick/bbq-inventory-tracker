package com.bbqreset.ui.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BBQTokens.colors.primary,
    onPrimary = BBQTokens.colors.primaryForeground,
    secondary = BBQTokens.colors.secondary,
    onSecondary = BBQTokens.colors.secondaryForeground,
    error = BBQTokens.colors.destructive,
    onError = BBQTokens.colors.destructiveForeground,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = BBQTokens.colors.border
)

private val DarkColors = darkColorScheme(
    primary = BBQTokens.colors.primary,
    onPrimary = BBQTokens.colors.primaryForeground,
    secondary = BBQTokens.colors.secondary,
    onSecondary = BBQTokens.colors.secondaryForeground,
    error = BBQTokens.colors.destructive,
    onError = BBQTokens.colors.destructiveForeground,
    background = BBQTokens.colors.background,
    onBackground = Color(0xFFE2E8F0),
    surface = BBQTokens.colors.surface,
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = BBQTokens.colors.surfaceVariant,
    onSurfaceVariant = Color(0xFFC7D2FE),
    outline = BBQTokens.colors.border
)

private val LocalSpacing = staticCompositionLocalOf { BBQSpacing }

private val LocalExtendedColors = staticCompositionLocalOf { BBQTokens.colors }

private val LocalTypography = staticCompositionLocalOf { BBQTokens.typography }

val MaterialTheme.spacing: BBQSpacing
    @Composable
    get() = LocalSpacing.current

val MaterialTheme.extendedColors: BBQColorScheme
    @Composable
    get() = LocalExtendedColors.current

val MaterialTheme.extendedTypography: BBQTypography
    @Composable
    get() = LocalTypography.current

@Composable
fun BBQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = if (darkTheme) DarkColors else LightColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides BBQSpacing,
        LocalExtendedColors provides BBQTokens.colors,
        LocalTypography provides BBQTokens.typography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MaterialTheme.typography.copy(),
            content = content
        )
    }
}
