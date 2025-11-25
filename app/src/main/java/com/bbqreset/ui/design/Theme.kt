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
import com.bbqreset.ui.design.system.*

private val LightColors = lightColorScheme(
    primary = BBQTokens.colors.primary,
    onPrimary = BBQTokens.colors.primaryForeground,
    secondary = BBQTokens.colors.secondary,
    onSecondary = BBQTokens.colors.secondaryForeground,
    error = BBQTokens.colors.destructive,
    onError = BBQTokens.colors.destructiveForeground,
    background = BBQTokens.colors.background,
    onBackground = Color(0xFF111111),
    surface = BBQTokens.colors.surface,
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = BBQTokens.colors.surfaceVariant,
    onSurfaceVariant = Color(0xFF666666),
    outline = BBQTokens.colors.border
)

private val DarkColors = darkColorScheme(
    primary = BBQTokens.colors.primary,
    onPrimary = BBQTokens.colors.primaryForeground,
    secondary = BBQTokens.colors.secondary,
    onSecondary = BBQTokens.colors.secondaryForeground,
    error = BBQTokens.colors.destructive,
    onError = BBQTokens.colors.destructiveForeground,
    background = Color(0xFF0D0D0D),
    onBackground = Color(0xFFEAEAEA),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFB3B3B3),
    outline = Color(0xFF333333)
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
        DSTheme(
            colors = LocalDSColors.current.copy(
                surface = colorScheme.surface,
                onSurface = colorScheme.onSurface,
                surfaceVariant = colorScheme.surfaceVariant,
                onSurfaceVariant = colorScheme.onSurfaceVariant,
                primary = colorScheme.primary,
                onPrimary = colorScheme.onPrimary
            )
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = MaterialTheme.typography.copy(),
                content = content
            )
        }
    }
}
