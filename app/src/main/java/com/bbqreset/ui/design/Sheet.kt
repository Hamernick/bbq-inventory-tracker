package com.bbqreset.ui.design

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberBBQSheetState(skipPartiallyExpanded: Boolean = true): SheetState {
    return rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = { true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BBQSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    dragHandle: (@Composable (() -> Unit))? = null,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = dragHandle
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun SheetState.open() {
    show()
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun SheetState.close() {
    hide()
}
