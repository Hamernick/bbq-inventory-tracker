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
        primary = Color(0xFF2563EB),
        primaryForeground = Color.White,
        secondary = Color(0xFF1E3A8A),
        secondaryForeground = Color.White,
        destructive = Color(0xFFDC2626),
        destructiveForeground = Color.White,
        background = Color(0xFFF8FAFC),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1F5F9),
        border = Color(0xFFE2E8F0),
        muted = Color(0xFFE2E8F0),
        mutedForeground = Color(0xFF475569),
        success = Color(0xFF16A34A),
        successForeground = Color.White,
        warning = Color(0xFFF97316),
        warningForeground = Color(0xFF1F2937)
    )

    val typography = BBQTypography(
        displayLarge = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        ),
        titleLarge = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        ),
        titleMedium = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937)
        ),
        titleSmall = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        ),
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937)
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF334155)
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF475569)
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
        )
    )
}
