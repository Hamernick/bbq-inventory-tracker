package com.bbqreset.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bbqreset.ui.design.BBQBadge
import com.bbqreset.ui.design.BBQButton
import com.bbqreset.ui.design.BBQButtonVariant
import com.bbqreset.ui.design.BBQCard
import com.bbqreset.ui.design.BBQTab
import com.bbqreset.ui.design.BBQTabs
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.design.BBQToastHost
import com.bbqreset.ui.design.extendedColors
import com.bbqreset.ui.design.extendedTypography
import com.bbqreset.ui.design.rememberBBQToastHostState
import com.bbqreset.ui.design.spacing
import kotlinx.coroutines.launch

data class TodayMenuItem(
    val id: Int,
    val name: String,
    val startQty: Int,
    val soldQty: Int,
    val alerts: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    items: List<TodayMenuItem> = sampleMenuItems
) {
    val tabIndex = rememberSaveable { mutableIntStateOf(0) }
    val toastHostState = rememberBBQToastHostState()
    val coroutineScope = rememberCoroutineScope()
    val tabs = remember {
        listOf(
            BBQTab("Today"),
            BBQTab("Templates"),
            BBQTab("Logs", badgeCount = 3)
        )
    }

    val showToast: (String, String) -> Unit = { message, label ->
        coroutineScope.launch {
            toastHostState.showSnackbar(message, actionLabel = label)
        }
    }

    Scaffold(
        topBar = {
            TodayTopBar(
                onSyncClick = {
                    showToast("Syncing inventory…", "warning")
                }
            )
        },
        snackbarHost = { BBQToastHost(hostState = toastHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.spacing.lg)
                .padding(vertical = MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
        ) {
            BBQTabs(
                tabs = tabs,
                selectedIndex = tabIndex.intValue,
                onSelectedChange = { tabIndex.intValue = it }
            )

            TodaySummaryCard(
                onAdjust = { showToast("Adjustment queued", "success") },
                onSoldOut = { showToast("Marked sold out", "destructive") }
            )

            Text(
                text = "Line items",
                style = MaterialTheme.extendedTypography.titleMedium
            )

            MenuItemList(
                items = items,
                onAdjust = { item ->
                    showToast("Adjusted ${item.name}", "success")
                }
            )

            BBQButton(
                text = "Add manual item",
                variant = BBQButtonVariant.OUTLINE,
                onClick = { showToast("Manual add coming soon", "warning") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodayTopBar(onSyncClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Today",
                style = MaterialTheme.extendedTypography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onSyncClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sync"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    )
}

@Composable
private fun TodaySummaryCard(
    onAdjust: () -> Unit,
    onSoldOut: () -> Unit
) {
    BBQCard(
        modifier = Modifier.fillMaxWidth(),
        tonal = true
    ) {
        Text(
            text = "Remaining brisket",
            style = MaterialTheme.extendedTypography.titleMedium
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Text(
                text = "42",
                style = MaterialTheme.extendedTypography.displayLarge
            )
            BBQBadge(
                text = "-6 vs start",
                background = MaterialTheme.extendedColors.warning,
                contentColor = MaterialTheme.extendedColors.warningForeground
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
            BBQButton(
                text = "Adjust",
                variant = BBQButtonVariant.SECONDARY,
                onClick = onAdjust
            )
            BBQButton(
                text = "Sold out",
                variant = BBQButtonVariant.DESTRUCTIVE,
                onClick = onSoldOut
            )
        }
    }
}

@Composable
private fun MenuItemList(
    items: List<TodayMenuItem>,
    onAdjust: (TodayMenuItem) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        modifier = Modifier.weight(1f, fill = false)
    ) {
        items(items, key = TodayMenuItem::id) { item ->
            MenuItemRow(
                item = item,
                onAdjust = { onAdjust(item) }
            )
        }
    }
}

@Composable
private fun MenuItemRow(
    item: TodayMenuItem,
    onAdjust: () -> Unit
) {
    BBQCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.extendedTypography.bodyLarge
                    )
                    Text(
                        text = "Start ${item.startQty} • Sold ${item.soldQty}",
                        style = MaterialTheme.extendedTypography.labelMedium
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    item.alerts.forEach { alert ->
                        BBQBadge(text = alert)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                BBQButton(
                    text = "Adjust",
                    variant = BBQButtonVariant.SECONDARY,
                    onClick = onAdjust,
                    modifier = Modifier.weight(1f)
                )
                BBQButton(
                    text = "Count",
                    variant = BBQButtonVariant.GHOST,
                    onClick = onAdjust,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private val sampleMenuItems = listOf(
    TodayMenuItem(
        id = 1,
        name = "Brisket Plate",
        startQty = 48,
        soldQty = 19,
        alerts = listOf("low")
    ),
    TodayMenuItem(
        id = 2,
        name = "Pulled Pork Sandwich",
        startQty = 64,
        soldQty = 32
    ),
    TodayMenuItem(
        id = 3,
        name = "Burnt Ends",
        startQty = 30,
        soldQty = 21,
        alerts = listOf("warn")
    )
)

@Preview(showBackground = true)
@Composable
fun TodayScreenPreview() {
    BBQTheme {
        TodayScreen()
    }
}
