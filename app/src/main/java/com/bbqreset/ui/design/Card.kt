package com.bbqreset.ui.design

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bbqreset.ui.design.system.LocalDSColors
import com.bbqreset.ui.design.system.LocalDSShapes

@Composable
fun BBQCard(
    modifier: Modifier = Modifier,
    tonal: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = LocalDSShapes.current.card
    val ds = LocalDSColors.current
    val background = if (tonal) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    // Tokenized border
    val borderStroke = BorderStroke(1.dp, ds.border)

    Surface(
        modifier = modifier,
        shape = shape,
        color = background,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = borderStroke
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.lg)) {
            content()
        }
    }
}
