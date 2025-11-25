package com.bbqreset.ui.design.system

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bbqreset.ui.design.BBQTokens

@Immutable
data class DSColors(
    val primary: Color,
    val onPrimary: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val border: Color,
    val muted: Color,
    val mutedForeground: Color,
    val destructive: Color,
    val onDestructive: Color
)

@Immutable
data class DSTypography(
    val display: androidx.compose.ui.text.TextStyle,
    val titleLg: androidx.compose.ui.text.TextStyle,
    val titleMd: androidx.compose.ui.text.TextStyle,
    val titleSm: androidx.compose.ui.text.TextStyle,
    val bodyLg: androidx.compose.ui.text.TextStyle,
    val bodyMd: androidx.compose.ui.text.TextStyle,
    val labelMd: androidx.compose.ui.text.TextStyle,
    val labelSm: androidx.compose.ui.text.TextStyle
)

@Immutable
data class DSSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp
)

@Immutable
data class DSRadius(
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val full: Dp = 999.dp
)

@Immutable
data class DSElevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp
)

@Immutable
data class DSMotion(
    val durationFast: Int = 120,
    val durationNormal: Int = 200,
    val durationSlow: Int = 320,
    val easeInOut: CubicBezierEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
)

@Immutable
data class DSShapes(
    val chip: Shape = RoundedCornerShape(8.dp),
    val container: Shape = RoundedCornerShape(12.dp),
    val card: Shape = RoundedCornerShape(16.dp)
)

// Locals
val LocalDSColors = staticCompositionLocalOf {
    DSColors(
        primary = BBQTokens.colors.primary,
        onPrimary = BBQTokens.colors.primaryForeground,
        surface = BBQTokens.colors.surface,
        onSurface = androidx.compose.ui.graphics.Color(0xFF1A1A1A),
        surfaceVariant = BBQTokens.colors.surfaceVariant,
        onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF666666),
        border = BBQTokens.colors.border,
        muted = BBQTokens.colors.muted,
        mutedForeground = BBQTokens.colors.mutedForeground,
        destructive = BBQTokens.colors.destructive,
        onDestructive = BBQTokens.colors.destructiveForeground
    )
}

val LocalDSTypography = staticCompositionLocalOf {
    val t = BBQTokens.typography
    DSTypography(
        display = t.displayLarge,
        titleLg = t.titleLarge,
        titleMd = t.titleMedium,
        titleSm = t.titleSmall,
        bodyLg = t.bodyLarge,
        bodyMd = t.bodyMedium,
        labelMd = t.labelMedium,
        labelSm = t.labelSmall
    )
}

val LocalDSSpacing = staticCompositionLocalOf { DSSpacing() }
val LocalDSRadius = staticCompositionLocalOf { DSRadius() }
val LocalDSElevation = staticCompositionLocalOf { DSElevation() }
val LocalDSMotion = staticCompositionLocalOf { DSMotion() }
val LocalDSShapes = staticCompositionLocalOf { DSShapes() }

@Composable
fun DSTheme(
    colors: DSColors = LocalDSColors.current,
    typography: DSTypography = LocalDSTypography.current,
    spacing: DSSpacing = LocalDSSpacing.current,
    radius: DSRadius = LocalDSRadius.current,
    elevation: DSElevation = LocalDSElevation.current,
    motion: DSMotion = LocalDSMotion.current,
    shapes: DSShapes = LocalDSShapes.current,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDSColors provides colors,
        LocalDSTypography provides typography,
        LocalDSSpacing provides spacing,
        LocalDSRadius provides radius,
        LocalDSElevation provides elevation,
        LocalDSMotion provides motion,
        LocalDSShapes provides shapes,
        content = content
    )
}
