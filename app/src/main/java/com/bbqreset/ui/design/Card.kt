package com.bbqreset.ui.design

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BBQCard(
    modifier: Modifier = Modifier,
    tonal: Boolean = false,
    content: @Composable () -> Unit
) {
    val background = if (tonal) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    Surface(
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        color = background,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = if (tonal) 6.dp else 0.dp,
        shadowElevation = if (tonal) 8.dp else 2.dp
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.lg)) {
            content()
        }
    }
}
