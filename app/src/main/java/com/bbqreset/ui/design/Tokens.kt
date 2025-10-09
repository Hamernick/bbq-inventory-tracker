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
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val labelMedium: TextStyle
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
        primary = Color(0xFF9C4221),
        primaryForeground = Color.White,
        secondary = Color(0xFF2C5282),
        secondaryForeground = Color.White,
        destructive = Color(0xFFB8322F),
        destructiveForeground = Color.White,
        background = Color(0xFF111827),
        surface = Color(0xFF1F2937),
        surfaceVariant = Color(0xFF273245),
        border = Color(0xFF374151),
        muted = Color(0xFF4B5563),
        mutedForeground = Color(0xFFD1D5DB),
        success = Color(0xFF2F855A),
        successForeground = Color.White,
        warning = Color(0xFFDD6B20),
        warningForeground = Color.Black
    )

    val typography = BBQTypography(
        displayLarge = TextStyle(
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        ),
        titleLarge = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        ),
        titleMedium = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFF3F4F6)
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFE5E7EB)
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9CA3AF)
        )
    )
}
