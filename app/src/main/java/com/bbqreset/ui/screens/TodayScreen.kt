package com.bbqreset.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import com.bbqreset.ui.design.BBQBadge
import com.bbqreset.ui.design.BBQButton
import com.bbqreset.ui.design.BBQButtonVariant
import com.bbqreset.ui.design.BBQCard
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.design.BBQToastHost
import com.bbqreset.ui.design.BBQInputField
import com.bbqreset.ui.design.extendedColors
import com.bbqreset.ui.design.extendedTypography
import com.bbqreset.ui.design.rememberBBQToastHostState
import com.bbqreset.ui.design.spacing
import java.util.Locale
import kotlinx.coroutines.launch

data class InventoryDay(val id: Int, val dayOfWeek: String, val dateNumber: String)

enum class InventoryStatus { NORMAL, LOW, SOLD_OUT }

data class InventoryEntry(
    val id: Int,
    val name: String,
    val sku: String,
    val unit: String,
    val start: Int,
    val sold: Int,
    val onHand: Int,
    val status: InventoryStatus,
    val threshold: Int
)

data class InventorySummary(
    val startTotal: Int,
    val soldTotal: Int,
    val onHandTotal: Int,
    val lowStockCount: Int
)

data class TodayUiState(
    val locationId: Long,
    val locationTimeZoneId: String,
    val headerTitle: String,
    val headerSubtitle: String,
    val locationName: String,
    val openStatusLabel: String,
    val nextResetLabel: String,
    val days: List<InventoryDay>,
    val selectedDayIndex: Int,
    val summary: InventorySummary,
    val entries: List<InventoryEntry>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    state: TodayUiState = sampleTodayUiState,
    onScheduleReset: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    onAdjustItem: (InventoryEntry) -> Unit = {}
) {
    val toastHostState = rememberBBQToastHostState()
    val coroutineScope = rememberCoroutineScope()
    val validatedIndex = remember(state.days, state.selectedDayIndex) {
        if (state.days.isEmpty()) {
            0
        } else {
            state.selectedDayIndex.coerceIn(state.days.indices)
        }
    }
    var selectedIndex by rememberSaveable(state.days.size, validatedIndex) {
        mutableIntStateOf(validatedIndex)
    }

    val showToast: (String, String) -> Unit = remember {
        { message, category ->
            coroutineScope.launch {
                toastHostState.showSnackbar(
                    message = message,
                    actionLabel = category
                )
            }
        }
    }

    // Local, mutable list of entries to reflect add flows in UI
    var localEntries by remember(state.entries) { mutableStateOf(state.entries) }

    // Bottom sheet and dialogs state
    val sheetState = com.bbqreset.ui.design.rememberBBQSheetState()
    var showAddSheet by remember { mutableStateOf(false) }
    var showNewItemDialog by remember { mutableStateOf(false) }
    var showReusePickerDialog by remember { mutableStateOf(false) }
    var confirmExistingItem by remember { mutableStateOf<InventoryEntry?>(null) }

    Scaffold(
        snackbarHost = { BBQToastHost(hostState = toastHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val screenWidthDp = LocalConfiguration.current.screenWidthDp
        val isWide = screenWidthDp >= 840

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.xl,
                vertical = MaterialTheme.spacing.xl
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
        ) {
            item(key = "header") {
                HeaderSection(
                    title = state.headerTitle,
                    subtitle = state.headerSubtitle,
                    locationName = state.locationName,
                    openStatusLabel = state.openStatusLabel,
                    nextResetLabel = state.nextResetLabel
                )
            }
            item(key = "week_strip") {
                WeekStrip(
                    days = state.days,
                    selectedIndex = selectedIndex,
                    onSelect = { index ->
                        selectedIndex = index
                        state.days.getOrNull(index)?.let {
                            showToast("Switched to ${it.dayOfWeek}", "info")
                        }
                    },
                    onNavigatePrevious = {
                        showToast("Loading previous week", "info")
                    },
                    onNavigateNext = {
                        showToast("Loading next week", "info")
                    }
                )
            }
            item(key = "inventory_table") {
                InventoryTableCard(
                    dateLabel = state.headerTitle,
                    entries = localEntries,
                    onAdd = {
                        showAddSheet = true
                    },
                    onExportCsv = onExportCsv,
                    onResetDay = onScheduleReset,
                    onRowAdjust = onAdjustItem
                )
            }
        }
    }

    if (showAddSheet) {
        com.bbqreset.ui.design.BBQSheet(
            onDismiss = { showAddSheet = false },
            sheetState = sheetState
        ) {
            AddItemSheetContent(
                onAddNew = {
                    showAddSheet = false
                    showNewItemDialog = true
                },
                onReuse = {
                    showAddSheet = false
                    showReusePickerDialog = true
                }
            )
        }
    }

    if (showNewItemDialog) {
        AddNewItemDialog(
            onDismiss = { showNewItemDialog = false },
            onConfirm = { name, sku, unit, qty ->
                val nextId = (localEntries.maxOfOrNull { it.id } ?: 0) + 1
                val threshold = 5
                val status = when {
                    qty <= 0 -> InventoryStatus.SOLD_OUT
                    qty <= threshold -> InventoryStatus.LOW
                    else -> InventoryStatus.NORMAL
                }
                localEntries = localEntries + InventoryEntry(
                    id = nextId,
                    name = name.trim(),
                    sku = if (sku.isBlank()) "Untracked SKU" else sku.trim(),
                    unit = unit.trim().ifBlank { "qty" },
                    start = qty,
                    sold = 0,
                    onHand = qty,
                    status = status,
                    threshold = threshold
                )
                showNewItemDialog = false
            }
        )
    }

    if (showReusePickerDialog) {
        ReuseItemPickerDialog(
            items = (state.entries.map { it.copy() }).distinctBy { it.name },
            onDismiss = { showReusePickerDialog = false },
            onPicked = { picked ->
                confirmExistingItem = picked
                showReusePickerDialog = false
            }
        )
    }

    confirmExistingItem?.let { picked ->
        ConfirmReuseDialog(
            item = picked,
            onDismiss = { confirmExistingItem = null },
            onConfirm = { qty ->
                val nextId = (localEntries.maxOfOrNull { it.id } ?: 0) + 1
                val threshold = picked.threshold
                val status = when {
                    qty <= 0 -> InventoryStatus.SOLD_OUT
                    qty <= threshold -> InventoryStatus.LOW
                    else -> InventoryStatus.NORMAL
                }
                localEntries = localEntries + picked.copy(
                    id = nextId,
                    start = qty,
                    sold = 0,
                    onHand = qty,
                    status = status
                )
                confirmExistingItem = null
            }
        )
    }
}

@Composable
private fun HeaderSection(
    title: String,
    subtitle: String,
    locationName: String,
    openStatusLabel: String,
    nextResetLabel: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        Text(
            text = title,
            style = MaterialTheme.extendedTypography.displayLarge
        )
        Text(
            text = subtitle,
            style = MaterialTheme.extendedTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            BBQBadge(text = locationName)
            BBQBadge(
                text = openStatusLabel,
                background = MaterialTheme.extendedColors.muted,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "Next reset: $nextResetLabel",
            style = MaterialTheme.extendedTypography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Removed SummaryCard and related metrics per request

@Composable
private fun ActionCard(
    onScheduleReset: () -> Unit,
    onApplyTemplate: () -> Unit,
    onExportCsv: () -> Unit
) {
    BBQCard {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Text(
                text = "Quick actions",
                style = MaterialTheme.extendedTypography.titleMedium
            )
            Text(
                text = "Run daily resets, apply templates, or share a CSV without leaving the floor.",
                style = MaterialTheme.extendedTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                BBQButton(
                    text = "Schedule daily reset",
                    onClick = onScheduleReset
                )
                BBQButton(
                    text = "Apply template",
                    variant = BBQButtonVariant.SECONDARY,
                    onClick = onApplyTemplate
                )
                BBQButton(
                    text = "Export CSV",
                    variant = BBQButtonVariant.OUTLINE,
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null
                        )
                    },
                    onClick = onExportCsv
                )
            }
        }
    }
}

@Composable
private fun InventoryEntryCard(
    entry: InventoryEntry,
    onAdjust: () -> Unit,
    onQuickSoldOut: () -> Unit
) {
    val tonal = entry.status != InventoryStatus.NORMAL
    BBQCard(tonal = tonal) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.extendedTypography.titleMedium
                    )
                    Text(
                        text = entry.sku,
                        style = MaterialTheme.extendedTypography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                InventoryStatusBadge(status = entry.status)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
            ) {
                InventoryStat(label = "Start", value = entry.start, unit = entry.unit)
                InventoryStat(label = "Sold", value = entry.sold, unit = entry.unit)
                InventoryStat(label = "On hand", value = entry.onHand, unit = entry.unit)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Threshold ${entry.threshold} ${entry.unit.lowercase(Locale.getDefault())}",
                    style = MaterialTheme.extendedTypography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    BBQButton(
                        text = "Adjust counts",
                        variant = BBQButtonVariant.SECONDARY,
                        onClick = onAdjust,
                        modifier = Modifier.widthIn(min = 140.dp)
                    )
                    BBQButton(
                        text = "Sold out",
                        variant = BBQButtonVariant.OUTLINE,
                        onClick = onQuickSoldOut,
                        modifier = Modifier.widthIn(min = 120.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryStat(
    label: String,
    value: Int,
    unit: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.extendedTypography.titleLarge
        )
        Text(
            text = "$label (${unit.uppercase(Locale.getDefault())})",
            style = MaterialTheme.extendedTypography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InventoryStatusBadge(status: InventoryStatus) {
    val label = when (status) {
        InventoryStatus.NORMAL -> "On track"
        InventoryStatus.LOW -> "Low stock"
        InventoryStatus.SOLD_OUT -> "Sold out"
    }
    BBQBadge(
        text = label,
        background = MaterialTheme.extendedColors.muted,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun WeekStrip(
    days: List<InventoryDay>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.extendedColors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigatePrevious,
                enabled = days.isNotEmpty()
            ) { Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous week") }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md)
            ) {
                itemsIndexed(days, key = { _, day -> day.id }) { index, day ->
                    DayChip(
                        day = day,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) }
                    )
                }
            }
            IconButton(
                onClick = onNavigateNext,
                enabled = days.isNotEmpty()
            ) { Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next week") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayChip(
    day: InventoryDay,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        color = background,
        contentColor = contentColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, MaterialTheme.extendedColors.border),
        shape = RoundedCornerShape(999.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            Text(
                text = day.dayOfWeek,
                style = MaterialTheme.extendedTypography.labelMedium
            )
            Text(
                text = day.dateNumber,
                style = MaterialTheme.extendedTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InventoryTableCard(
    dateLabel: String,
    entries: List<InventoryEntry>,
    onAdd: () -> Unit,
    onExportCsv: () -> Unit,
    onResetDay: () -> Unit,
    onRowAdjust: (InventoryEntry) -> Unit
) {
    BBQCard {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .height(16.dp)
                        .widthIn(min = 16.dp)
                        .background(MaterialTheme.extendedColors.muted, CircleShape)
                )
                Text(
                    text = "Inventory — $dateLabel",
                    style = MaterialTheme.extendedTypography.titleLarge
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onAdd) { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") }
                var menuOpen by remember { mutableStateOf(false) }
                IconButton(onClick = { menuOpen = true }) { Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More") }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Download report (CSV)") },
                        onClick = { menuOpen = false; onExportCsv() }
                    )
                    DropdownMenuItem(
                        text = { Text("Reset Day") },
                        onClick = { menuOpen = false; onResetDay() }
                    )
                }
            }
        }

        // Table header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.extendedColors.border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Item", modifier = Modifier.weight(0.5f), style = MaterialTheme.extendedTypography.labelMedium)
                Text("Unit", modifier = Modifier.weight(0.2f), style = MaterialTheme.extendedTypography.labelMedium)
                Text("Start", modifier = Modifier.weight(0.1f), style = MaterialTheme.extendedTypography.labelMedium)
                Text("Sold", modifier = Modifier.weight(0.1f), style = MaterialTheme.extendedTypography.labelMedium)
                Text("On hand", modifier = Modifier.weight(0.1f), style = MaterialTheme.extendedTypography.labelMedium)
            }
        }

        // Rows
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.5f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(entry.name, style = MaterialTheme.extendedTypography.bodyLarge)
                        Text(entry.sku, style = MaterialTheme.extendedTypography.labelSmall, color = MaterialTheme.extendedColors.mutedForeground)
                    }
                    Text(entry.unit.uppercase(Locale.getDefault()), modifier = Modifier.weight(0.2f))
                    Text(entry.start.toString(), modifier = Modifier.weight(0.1f))
                    Text(entry.sold.toString(), modifier = Modifier.weight(0.1f))
                    Text(entry.onHand.toString(), modifier = Modifier.weight(0.1f), fontWeight = FontWeight.SemiBold)
                }
                Divider(color = MaterialTheme.extendedColors.border)
            }
        }
    }
}

val sampleTodayUiState = TodayUiState(
    locationId = 1L,
    locationTimeZoneId = "America/Chicago",
    headerTitle = "Today",
    headerSubtitle = "Reset counts, monitor sell-through, and keep the pit stocked.",
    locationName = "Downtown BBQ",
    openStatusLabel = "Open - Closes 9:00 PM",
    nextResetLabel = "Tomorrow 4:30 AM",
    days = listOf(
        InventoryDay(1, "Wed", "08"),
        InventoryDay(2, "Thu", "09"),
        InventoryDay(3, "Fri", "10"),
        InventoryDay(4, "Sat", "11"),
        InventoryDay(5, "Sun", "12"),
        InventoryDay(6, "Mon", "13"),
        InventoryDay(7, "Tue", "14")
    ),
    selectedDayIndex = 2,
    summary = InventorySummary(
        startTotal = 136,
        soldTotal = 62,
        onHandTotal = 74,
        lowStockCount = 1
    ),
    entries = listOf(
        InventoryEntry(
            id = 1,
            name = "Smoked Brisket",
            sku = "SKU BBQ-001",
            unit = "lbs",
            start = 48,
            sold = 22,
            onHand = 26,
            status = InventoryStatus.NORMAL,
            threshold = 6
        ),
        InventoryEntry(
            id = 2,
            name = "Spare Ribs Half Rack",
            sku = "SKU BBQ-004",
            unit = "racks",
            start = 36,
            sold = 28,
            onHand = 8,
            status = InventoryStatus.LOW,
            threshold = 10
        ),
        InventoryEntry(
            id = 3,
            name = "Cornbread Loaf",
            sku = "SKU BAK-102",
            unit = "items",
            start = 52,
            sold = 12,
            onHand = 40,
            status = InventoryStatus.NORMAL,
            threshold = 12
        ),
        InventoryEntry(
            id = 4,
            name = "Banana Pudding Cups",
            sku = "SKU DES-210",
            unit = "cups",
            start = 24,
            sold = 24,
            onHand = 0,
            status = InventoryStatus.SOLD_OUT,
            threshold = 6
        )
    )
)

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFC)
@Composable
fun TodayScreenPreview() {
    BBQTheme(darkTheme = false) {
        TodayScreen()
    }
}

@Composable
private fun AddItemSheetContent(
    onAddNew: () -> Unit,
    onReuse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
            Text(
                text = "Add inventory",
                style = MaterialTheme.extendedTypography.titleLarge
            )
            Text(
                text = "Add a brand new product or reuse a previous one.",
                style = MaterialTheme.extendedTypography.bodyMedium,
                color = MaterialTheme.extendedColors.mutedForeground
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
            BBQButton(
                text = "Add new item",
                onClick = onAddNew
            )
            BBQButton(
                text = "Reuse item",
                variant = BBQButtonVariant.SECONDARY,
                onClick = onReuse
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddNewItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, sku: String, unit: String, qty: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("qty") }
    var qtyText by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            val qty = qtyText.toIntOrNull()
            val enabled = name.isNotBlank() && qty != null && qty >= 0
            BBQButton(
                text = "Add",
                onClick = { onConfirm(name, sku, unit, qty!!) },
                enabled = enabled
            )
        },
        dismissButton = {
            BBQButton(
                text = "Cancel",
                variant = BBQButtonVariant.SECONDARY,
                onClick = onDismiss
            )
        },
        title = { Text("Add new item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                BBQInputField(value = name, onValueChange = { name = it }, label = "Product name")
                BBQInputField(value = sku, onValueChange = { sku = it }, label = "SKU (optional)")
                BBQInputField(value = unit, onValueChange = { unit = it }, label = "Unit (e.g. lbs, items)")
                BBQInputField(value = qtyText, onValueChange = { qtyText = it.filter { ch -> ch.isDigit() } }, label = "Starting stock")
            }
        }
    )
}

@Composable
private fun ReuseItemPickerDialog(
    items: List<InventoryEntry>,
    onDismiss: () -> Unit,
    onPicked: (InventoryEntry) -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.lg, vertical = MaterialTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                Text(text = "Reuse item", style = MaterialTheme.extendedTypography.titleLarge)
                Text(
                    text = "Choose a previous product to add again.",
                    style = MaterialTheme.extendedTypography.bodyMedium,
                    color = MaterialTheme.extendedColors.mutedForeground
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .border(1.dp, MaterialTheme.extendedColors.border, RoundedCornerShape(8.dp))
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(items, key = { it.id }) { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(entry.name, style = MaterialTheme.extendedTypography.bodyLarge)
                                    Text(entry.sku, style = MaterialTheme.extendedTypography.labelSmall, color = MaterialTheme.extendedColors.mutedForeground)
                                }
                                BBQButton(text = "Select", variant = BBQButtonVariant.OUTLINE) {
                                    onPicked(entry)
                                }
                            }
                            Divider(color = MaterialTheme.extendedColors.border)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    BBQButton(text = "Close", variant = BBQButtonVariant.SECONDARY, onClick = onDismiss)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmReuseDialog(
    item: InventoryEntry,
    onDismiss: () -> Unit,
    onConfirm: (qty: Int) -> Unit
) {
    var qtyText by remember { mutableStateOf(item.start.toString()) }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            val qty = qtyText.toIntOrNull()
            val enabled = qty != null && qty >= 0
            BBQButton(text = "Add", onClick = { onConfirm(qty!!) }, enabled = enabled)
        },
        dismissButton = {
            BBQButton(text = "Cancel", variant = BBQButtonVariant.SECONDARY, onClick = onDismiss)
        },
        title = { Text("Confirm item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                Text(item.name, style = MaterialTheme.extendedTypography.bodyLarge)
                Text(item.sku, style = MaterialTheme.extendedTypography.labelSmall, color = MaterialTheme.extendedColors.mutedForeground)
                BBQInputField(value = qtyText, onValueChange = { qtyText = it.filter { ch -> ch.isDigit() } }, label = "Starting stock")
            }
        }
    )
}
