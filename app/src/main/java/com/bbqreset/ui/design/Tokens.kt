package com.bbqreset.ui.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BBQColorScheme(
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val muted: Color,
    val mutedForeground: Color,
    val success: Color,
    val successForeground: Color,
    val warning: Color,
    val warningForeground: Color
)

data class BBQTypography(
    val displayLarge: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle
)

object BBQSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
}

object BBQTokens {
    val colors = BBQColorScheme(
        // Grayscale palette
        primary = Color(0xFF000000),
        primaryForeground = Color(0xFFFFFFFF),
        secondary = Color(0xFF333333),
        secondaryForeground = Color(0xFFFFFFFF),
        destructive = Color(0xFF333333),
        destructiveForeground = Color(0xFFFFFFFF),
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF2F2F2),
        border = Color(0xFFE0E0E0),
        muted = Color(0xFFEAEAEA),
        mutedForeground = Color(0xFF444444),
        success = Color(0xFF333333),
        successForeground = Color(0xFFFFFFFF),
        warning = Color(0xFF333333),
        warningForeground = Color(0xFFFFFFFF)
    )

    val typography = BBQTypography(
        displayLarge = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111111)
        ),
        titleLarge = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        ),
        titleMedium = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        ),
        titleSmall = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4D4D4D)
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF666666)
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF777777)
        )
    )
}
