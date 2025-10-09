package com.bbqreset.ui.design

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun rememberBBQToastHostState(): SnackbarHostState = remember { SnackbarHostState() }

@Composable
fun BBQToastHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        BBQToast(data = data)
    }
}

@Composable
private fun BBQToast(data: SnackbarData) {
    val visuals = data.visuals
    // Grayscale, no status colors
    val background = MaterialTheme.colorScheme.surfaceVariant
    val content = MaterialTheme.colorScheme.onSurface

    Snackbar(
        containerColor = background,
        contentColor = content,
        dismissActionContentColor = content,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = visuals.message,
            style = MaterialTheme.extendedTypography.bodyMedium
        )
    }
}
