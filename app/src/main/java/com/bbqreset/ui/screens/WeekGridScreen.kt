@file:Suppress("FunctionNaming", "LongParameterList", "MagicNumber")

package com.bbqreset.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.bbqreset.ui.design.BBQTheme
import com.bbqreset.ui.vm.AuthState
import com.bbqreset.ui.vm.CatalogItemUi
import com.bbqreset.ui.vm.DayKey
import com.bbqreset.ui.vm.QuantityField
import com.bbqreset.ui.vm.WeekGridUiState
import com.bbqreset.ui.vm.WeekItemUi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun WeekGridScreen(
    state: WeekGridUiState,
    authState: AuthState? = null,
    onStartAuth: () -> Unit = {},
    onConnect: () -> Unit,
    onOpenSettings: () -> Unit,
    onCreateItem: (String) -> Unit,
    onReseedSample: () -> Unit,
    onSelectLocation: (Long) -> Unit,
    onWeekPicked: (LocalDate) -> Unit,
    onToggleSelect: (Long) -> Unit,
    onAddItems: (List<Long>) -> Unit,
    onDeleteSelected: () -> Unit,
    onUpdateQuantity: (Long, QuantityField, Int) -> Unit,
    onApply: () -> Unit
) {
    if (!state.connected) {
        ConnectScreen(
            authState = authState,
            onStartAuth = onStartAuth,
            onConnect = onConnect,
            onOpenSettings = onOpenSettings,
            onReseedSample = onReseedSample
        )
        return
    }

    MainGridScreen(
        state = state,
        onSelectLocation = onSelectLocation,
        onWeekPicked = onWeekPicked,
        onToggleSelect = onToggleSelect,
        onAddItems = onAddItems,
        onCreateItem = onCreateItem,
        onDeleteSelected = onDeleteSelected,
        onUpdateQuantity = onUpdateQuantity,
        onApply = onApply,
        onOpenSettings = onOpenSettings
    )
}

@Composable
private fun ConnectScreen(
    authState: AuthState?,
    onStartAuth: () -> Unit,
    onConnect: () -> Unit,
    onOpenSettings: () -> Unit,
    onReseedSample: () -> Unit
) {
    val painter = rememberAsyncImagePainter(
        model = "https://images.unsplash.com/photo-1602146057681-08560aee8cde?auto=format&fit=crop&w=1200&q=80"
    )
    val context = LocalContext.current

    LaunchedEffect(authState?.authUrl) {
        val url = authState?.authUrl
        if (!url.isNullOrBlank()) {
            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        val showImage = painter.state is AsyncImagePainter.State.Success
        if (showImage) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                ConnectCard(authState = authState, onStartAuth = onStartAuth, onOpenSettings = onOpenSettings)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    androidx.compose.foundation.Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ConnectCard(
                    authState = authState,
                    onStartAuth = onStartAuth,
                    onOpenSettings = onOpenSettings,
                    fillWidth = true
                )
            }
        }
    }
}

@Composable
private fun ConnectCard(
    authState: AuthState?,
    onStartAuth: () -> Unit,
    onOpenSettings: () -> Unit,
    fillWidth: Boolean = false
) {
    val cardModifier = if (fillWidth) {
        Modifier
            .fillMaxWidth(0.5f)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    } else {
        Modifier
            .width(320.dp)
            .padding(28.dp)
    }
    Card(modifier = cardModifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Widgets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = "Inventory Scheduler",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Link Clover and start planning your weekly stock—no spreadsheets needed.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                modifier = Modifier
                    .width(240.dp)
                    .height(48.dp),
                enabled = authState?.loading != true,
                onClick = onStartAuth,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text("Connect to Clover")
            }
            if (authState?.error != null) {
                Text(
                    text = authState.error,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainGridScreen(
    state: WeekGridUiState,
    onSelectLocation: (Long) -> Unit,
    onWeekPicked: (LocalDate) -> Unit,
    onToggleSelect: (Long) -> Unit,
    onAddItems: (List<Long>) -> Unit,
    onCreateItem: (String) -> Unit,
    onDeleteSelected: () -> Unit,
    onUpdateQuantity: (Long, QuantityField, Int) -> Unit,
    onApply: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var addDialogOpen by remember { mutableStateOf(false) }
    var deleteDialogOpen by remember { mutableStateOf(false) }
    var editDialogOpen by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf(0) }
    var editLabel by remember { mutableStateOf("") }
    var editTarget by remember { mutableStateOf<Pair<Long, QuantityField>?>(null) }
    var locationMenuOpen by remember { mutableStateOf(false) }
    var weekPickerOpen by remember { mutableStateOf(false) }
    var catalogSelection by remember { mutableStateOf(setOf<Long>()) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    LaunchedEffect(state.weekStart) {
        datePickerState.selectedDateMillis =
            state.weekStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    LaunchedEffect(editTarget) {
        if (editTarget == null) editDialogOpen = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 0.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = { locationMenuOpen = true }) {
                    Text(state.locationName)
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                DropdownMenu(
                    expanded = locationMenuOpen,
                    onDismissRequest = { locationMenuOpen = false }
                ) {
                    state.locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location.name) },
                            onClick = {
                                onSelectLocation(location.id)
                                locationMenuOpen = false
                            }
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { weekPickerOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Pick week"
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
                IconButton(onClick = onApply) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Apply changes"
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Week",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Week",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(onClick = { weekPickerOpen = true }) {
                Text(state.weekLabel)
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Inventory",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { addDialogOpen = true },
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Item")
                }
                IconButton(
                    onClick = { deleteDialogOpen = true },
                    enabled = state.selectedIds.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete selected"
                    )
                }
            }
        }

        HeaderRow()
        Divider()
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(
                items = state.items,
                key = { it.itemId }
            ) { item ->
                ItemRow(
                    item = item,
                    selected = state.selectedIds.contains(item.itemId),
                    onToggleSelect = { onToggleSelect(item.itemId) },
                    onEdit = { field, label, value ->
                        editDialogOpen = true
                        editValue = value
                        editLabel = label
                        editTarget = item.itemId to field
                    }
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onApply) { Text("Apply") }
        }
    }

    if (weekPickerOpen) {
        DatePickerDialog(
            onDismissRequest = { weekPickerOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            onWeekPicked(date)
                        }
                        weekPickerOpen = false
                    }
                ) { Text("Apply") }
            },
            dismissButton = { TextButton(onClick = { weekPickerOpen = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (addDialogOpen) {
        AddItemDialog(
            catalog = state.catalog,
            selectedIds = catalogSelection,
            onToggle = { id ->
                catalogSelection = catalogSelection.toMutableSet().apply {
                    if (contains(id)) remove(id) else add(id)
                }
            },
            onDismiss = { addDialogOpen = false },
            onAdd = {
                onAddItems(catalogSelection.toList())
                catalogSelection = emptySet()
                addDialogOpen = false
            },
            onCreate = { name ->
                onCreateItem(name)
                addDialogOpen = false
            }
        )
    }

    if (deleteDialogOpen) {
        AlertDialog(
            onDismissRequest = { deleteDialogOpen = false },
            title = { Text("Remove selected items?") },
            text = { Text("This only removes rows from this planner, not from Clover inventory.") },
            confirmButton = {
                TextButton(
                    enabled = state.selectedIds.isNotEmpty(),
                    onClick = {
                        onDeleteSelected()
                        deleteDialogOpen = false
                    }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { deleteDialogOpen = false }) { Text("Cancel") }
            }
        )
    }

    if (editDialogOpen && editTarget != null) {
        AlertDialog(
            onDismissRequest = {
                editDialogOpen = false
                editTarget = null
            },
            title = { Text(editLabel.ifBlank { "Edit Quantity" }) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { editValue = (editValue - 1).coerceAtLeast(0) }
                    ) { Text("−") }
                    OutlinedTextField(
                        value = editValue.toString(),
                        onValueChange = { value -> editValue = value.toIntOrNull() ?: 0 },
                        singleLine = true,
                        modifier = Modifier.width(96.dp)
                    )
                    OutlinedButton(onClick = { editValue += 1 }) { Text("+") }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = editTarget
                        if (target != null) {
                            onUpdateQuantity(target.first, target.second, editValue)
                        }
                        editDialogOpen = false
                        editTarget = null
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        editDialogOpen = false
                        editTarget = null
                    }
                ) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("", modifier = Modifier.width(28.dp))
        Text(
            text = "Item",
            modifier = Modifier.weight(3f),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Default",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayKey.entries.forEach { day ->
            Text(
                text = day.name.lowercase().replaceFirstChar { it.uppercase() },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ItemRow(
    item: WeekItemUi,
    selected: Boolean,
    onToggleSelect: () -> Unit,
    onEdit: (QuantityField, String, Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = { onToggleSelect() }
        )
        Column(modifier = Modifier.weight(3f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    append(item.unitType.ifBlank { "count" })
                    item.sku?.takeIf { it.isNotBlank() }?.let { append(" - ").append(it) }
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        QtyPill(
            label = item.defaultQty.toString(),
            modifier = Modifier.weight(1f),
            onClick = { onEdit(QuantityField.Default, "${item.name} - Default", item.defaultQty) }
        )
        DayKey.entries.forEach { day ->
            val qty = item.days[day] ?: 0
            QtyPill(
                label = qty.toString(),
                modifier = Modifier.weight(1f),
                onClick = { onEdit(QuantityField.Day(day), "${item.name} - ${day.name}", qty) }
            )
        }
    }
}

@Composable
private fun QtyPill(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun AddItemDialog(
    catalog: List<CatalogItemUi>,
    selectedIds: Set<Long>,
    onToggle: (Long) -> Unit,
    onDismiss: () -> Unit,
    onAdd: () -> Unit,
    onCreate: (String) -> Unit
) {
    var newItemName by remember { mutableStateOf("") }
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .width(420.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Select or Create Items", style = MaterialTheme.typography.titleMedium)
                Divider()
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("New item name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            onCreate(newItemName)
                            newItemName = ""
                        }
                    ) { Text("Create Item") }
                }
                LazyColumn(
                    modifier = Modifier.height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(catalog, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Unit: ${item.unitType.ifBlank { "count" }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Checkbox(
                            checked = selectedIds.contains(item.id),
                            onCheckedChange = { onToggle(item.id) }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onAdd) { Text("Add Selected") }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekGridPreview() {
    val sampleState = WeekGridUiState(
        connected = true,
        locationId = 1L,
        locationName = "Downtown BBQ",
        locations = listOf(
            com.bbqreset.ui.vm.LocationUi(1, "Downtown BBQ"),
            com.bbqreset.ui.vm.LocationUi(2, "Uptown BBQ")
        ),
        weekStart = LocalDate.of(2025, 10, 6),
        weekEnd = LocalDate.of(2025, 10, 12),
        items = listOf(
            WeekItemUi(
                itemId = 1,
                name = "Ribs",
                sku = "RIB-001",
                unitType = "count",
                defaultQty = 24,
                days = DayKey.entries.associateWith { 0 }
            ),
            WeekItemUi(
                itemId = 2,
                name = "Brisket",
                sku = "BRI-002",
                unitType = "lbs",
                defaultQty = 12,
                days = DayKey.entries.associateWith { day ->
                    when (day) {
                        DayKey.FRI -> 7
                        DayKey.SAT -> 8
                        else -> 4
                    }
                }
            )
        ),
        selectedIds = emptySet(),
        catalog = emptyList()
    )
    BBQTheme {
        WeekGridScreen(
            state = sampleState,
            onConnect = {},
            onOpenSettings = {},
            onCreateItem = {},
            onReseedSample = {},
            onSelectLocation = {},
            onWeekPicked = {},
            onToggleSelect = {},
            onAddItems = {},
            onDeleteSelected = {},
            onUpdateQuantity = { _, _, _ -> },
            onApply = {}
        )
    }
}
