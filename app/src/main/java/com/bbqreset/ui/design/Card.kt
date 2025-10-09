package com.bbqreset.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BBQCard(
    modifier: Modifier = Modifier,
    tonal: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val background = if (tonal) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderStroke = if (tonal) {
        null
    } else {
        BorderStroke(1.dp, MaterialTheme.extendedColors.border)
    }

    Surface(
        modifier = modifier,
        shape = shape,
        color = background,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = if (tonal) 2.dp else 0.dp,
        shadowElevation = if (tonal) 0.dp else 4.dp,
        border = borderStroke
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.lg)) {
            content()
        }
    }
}
