package com.bbqreset.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bbqreset.ui.design.BBQButton
import com.bbqreset.ui.design.BBQButtonVariant
import com.bbqreset.ui.design.BBQCard
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.design.BBQToastHost
import com.bbqreset.ui.design.extendedColors
import com.bbqreset.ui.design.extendedTypography
import com.bbqreset.ui.design.rememberBBQToastHostState
import com.bbqreset.ui.design.spacing
import kotlinx.coroutines.launch

data class InventoryDay(
    val id: Int,
    val dayOfWeek: String,
    val dateNumber: String
)

data class InventoryEntry(
    val id: Int,
    val name: String,
    val sku: String,
    val unit: String,
    val start: Int,
    val sold: Int,
    val onHand: Int
)

data class TodayUiState(
    val headerTitle: String,
    val headerSubtitle: String,
    val days: List<InventoryDay>,
    val selectedDayIndex: Int,
    val entries: List<InventoryEntry>
)

@Composable
fun TodayScreen(
    state: TodayUiState = sampleTodayUiState
) {
    val toastHostState = rememberBBQToastHostState()
    val coroutineScope = rememberCoroutineScope()
    val safeIndex = remember(state.days, state.selectedDayIndex) {
        if (state.days.isEmpty()) {
            0
        } else {
            state.selectedDayIndex.coerceIn(state.days.indices)
        }
    }
    var selectedIndex by rememberSaveable(state.days.size, safeIndex) {
        mutableIntStateOf(safeIndex)
    }

    val showToast: (String, String) -> Unit = remember {
        { message, label ->
            coroutineScope.launch {
                toastHostState.showSnackbar(message, actionLabel = label)
            }
        }
    }

    Scaffold(
        snackbarHost = { BBQToastHost(hostState = toastHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.spacing.xl)
                .padding(vertical = MaterialTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xl)
        ) {
            Text(
                text = "BBQ Inventory",
                style = MaterialTheme.extendedTypography.displayLarge
            )

            WeekStrip(
                days = state.days,
                selectedIndex = selectedIndex,
                onSelect = { index ->
                    selectedIndex = index
                    showToast("Switched to ${state.days[index].dayOfWeek}", "success")
                },
                onNavigatePrevious = {
                    showToast("Loading previous week", "warning")
                },
                onNavigateNext = {
                    showToast("Loading next week", "warning")
                }
            )

            InventoryCard(
                title = state.headerTitle,
                subtitle = state.headerSubtitle,
                entries = state.entries,
                onAddItem = { showToast("Add inventory item", "warning") },
                onMoreActions = { showToast("More actions coming soon", "warning") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                BBQButton(
                    text = "Download report (CSV)",
                    variant = BBQButtonVariant.OUTLINE,
                    onClick = { showToast("Export scheduled", "success") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun WeekStrip(
    days: List<InventoryDay>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        IconButton(onClick = onNavigatePrevious, enabled = days.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous week"
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(days, key = { _, day -> day.id }) { index, day ->
                DayChip(
                    day = day,
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) }
                )
            }
        }

        IconButton(onClick = onNavigateNext, enabled = days.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next week"
            )
        }
    }
}

@Composable
private fun DayChip(
    day: InventoryDay,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.extendedColors.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        MaterialTheme.extendedColors.primaryForeground
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val borderColor = if (selected) {
        Color.Transparent
    } else {
        MaterialTheme.extendedColors.border
    }

    Surface(
        onClick = onClick,
        enabled = true,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = if (selected) 4.dp else 0.dp,
        shadowElevation = if (selected) 2.dp else 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.lg, vertical = MaterialTheme.spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            Text(
                text = day.dayOfWeek.uppercase(),
                style = MaterialTheme.extendedTypography.labelMedium.copy(
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = day.dateNumber,
                style = MaterialTheme.extendedTypography.titleSmall.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InventoryCard(
    title: String,
    subtitle: String,
    entries: List<InventoryEntry>,
    onAddItem: () -> Unit,
    onMoreActions: () -> Unit
) {
    BBQCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                    Text(
                        text = title,
                        style = MaterialTheme.extendedTypography.titleLarge
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.extendedTypography.bodyMedium
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    IconButton(onClick = onAddItem) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add inventory item"
                        )
                    }
                    IconButton(onClick = onMoreActions) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More actions"
                        )
                    }
                }
            }

            InventoryTableHeader()

            entries.forEachIndexed { index, entry ->
                InventoryTableRow(entry)
                if (index < entries.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.extendedColors.border.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun InventoryTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderCell(text = "Item", weight = 0.4f)
        HeaderCell(text = "Unit", weight = 0.16f, textAlign = TextAlign.Center)
        HeaderCell(text = "Start", weight = 0.15f, textAlign = TextAlign.Center)
        HeaderCell(text = "Sold", weight = 0.15f, textAlign = TextAlign.Center)
        HeaderCell(text = "On Hand", weight = 0.14f, textAlign = TextAlign.Center)
    }
}

@Composable
private fun HeaderCell(
    text: String,
    weight: Float,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = text.uppercase(),
        modifier = Modifier
            .weight(weight)
            .padding(vertical = MaterialTheme.spacing.xs),
        style = MaterialTheme.extendedTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = textAlign
    )
}

@Composable
private fun InventoryTableRow(entry: InventoryEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemCell(entry, modifier = Modifier.weight(0.4f))
        ValueCell(entry.unit, modifier = Modifier.weight(0.16f))
        ValueCell(entry.start.toString(), modifier = Modifier.weight(0.15f))
        ValueCell(entry.sold.toString(), modifier = Modifier.weight(0.15f))
        ValueCell(entry.onHand.toString(), modifier = Modifier.weight(0.14f))
    }
}

@Composable
private fun ItemCell(entry: InventoryEntry, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
        Text(
            text = entry.name,
            style = MaterialTheme.extendedTypography.bodyLarge
        )
        Text(
            text = entry.sku,
            style = MaterialTheme.extendedTypography.labelSmall
        )
    }
}

@Composable
private fun ValueCell(value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.extendedTypography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

private val sampleTodayUiState = TodayUiState(
    headerTitle = "Inventory — 10-08-2025",
    headerSubtitle = "Set your daily counts and watch sales update in real time.",
    days = listOf(
        InventoryDay(id = 1, dayOfWeek = "Wed", dateNumber = "8"),
        InventoryDay(id = 2, dayOfWeek = "Thu", dateNumber = "9"),
        InventoryDay(id = 3, dayOfWeek = "Fri", dateNumber = "10"),
        InventoryDay(id = 4, dayOfWeek = "Sat", dateNumber = "11"),
        InventoryDay(id = 5, dayOfWeek = "Sun", dateNumber = "12"),
        InventoryDay(id = 6, dayOfWeek = "Mon", dateNumber = "13"),
        InventoryDay(id = 7, dayOfWeek = "Tue", dateNumber = "14")
    ),
    selectedDayIndex = 0,
    entries = listOf(
        InventoryEntry(
            id = 1,
            name = "Brisket",
            sku = "SKU: BBQ-001",
            unit = "LBS",
            start = 50,
            sold = 0,
            onHand = 50
        ),
        InventoryEntry(
            id = 2,
            name = "Ribs Half Rack",
            sku = "SKU: BBQ-004",
            unit = "LBS",
            start = 42,
            sold = 0,
            onHand = 42
        ),
        InventoryEntry(
            id = 3,
            name = "Cornbread Loaf",
            sku = "SKU: BAK-102",
            unit = "ITEMS",
            start = 24,
            sold = 0,
            onHand = 24
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
