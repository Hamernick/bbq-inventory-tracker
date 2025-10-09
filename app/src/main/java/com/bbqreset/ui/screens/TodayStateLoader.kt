package com.bbqreset.ui.screens

import com.bbqreset.data.db.AppDatabase
import com.bbqreset.data.db.entity.CounterEntity
import com.bbqreset.data.db.entity.ItemEntity
import com.bbqreset.data.db.entity.TemplateItemEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import java.time.temporal.TemporalAdjusters

suspend fun loadTodayUiState(database: AppDatabase): TodayUiState {
    val location = database.locationDao().listLocations().firstOrNull()
        ?: return sampleTodayUiState

    val mostRecentDate = database.counterDao().getMostRecentDate(location.id)
        ?: LocalDate.now().toString()

    val counters = database.counterDao().listCountersForDate(location.id, mostRecentDate)
    val items = database.itemDao().listItemsForLocation(location.id).associateBy { it.id }

    val template = database.templateDao().listTemplates(location.id).firstOrNull()
    val templateItems = template
        ?.let { database.templateItemDao().listForTemplate(it.id) }
        ?.associateBy { it.itemId }
        .orEmpty()

    val anchorDate = runCatching { LocalDate.parse(mostRecentDate) }.getOrElse { LocalDate.now() }
    val startOfWeek = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val formatter = DateTimeFormatter.ofPattern("dd")
    val locale = Locale.getDefault()

    val days = (0 until 7).map { offset ->
        val day = startOfWeek.plusDays(offset.toLong())
        InventoryDay(
            id = offset,
            dayOfWeek = day.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
            dateNumber = day.format(formatter)
        )
    }

    val selectedIndex = (anchorDate.dayOfWeek.value - 1).coerceIn(0, 6)

    val entries = counters.mapNotNull { counter ->
        val item = items[counter.itemId] ?: return@mapNotNull null
        counter.toInventoryEntry(
            item = item,
            templateItem = templateItems[counter.itemId]
        )
    }

    val summary = InventorySummary(
        startTotal = counters.sumOf { it.startQuantity },
        soldTotal = counters.sumOf { it.soldQuantity },
        onHandTotal = counters.sumOf { it.startQuantity - it.soldQuantity + it.manualAdjustment },
        lowStockCount = entries.count { it.status == InventoryStatus.LOW || it.status == InventoryStatus.SOLD_OUT }
    )

    return TodayUiState(
        locationId = location.id,
        locationTimeZoneId = location.timeZoneId,
        headerTitle = anchorDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
        headerSubtitle = "Reset counts, monitor sell-through, and keep the pit stocked.",
        locationName = location.name,
        openStatusLabel = "Open - Closes 9:00 PM",
        nextResetLabel = "${anchorDate.plusDays(1).format(DateTimeFormatter.ofPattern("EEE"))} 4:30 AM",
        days = days,
        selectedDayIndex = selectedIndex,
        summary = summary,
        entries = entries
    )
}

private fun CounterEntity.toInventoryEntry(
    item: ItemEntity,
    templateItem: TemplateItemEntity?
): InventoryEntry {
    val onHand = startQuantity - soldQuantity + manualAdjustment
    val threshold = max(1, templateItem?.startQuantity?.let { ceil(it * 0.2).toInt() } ?: 5)
    val status = when {
        onHand <= 0 -> InventoryStatus.SOLD_OUT
        onHand <= threshold -> InventoryStatus.LOW
        else -> InventoryStatus.NORMAL
    }

    return InventoryEntry(
        id = item.id.toInt(),
        name = item.name,
        sku = item.sku?.let { "SKU: $it" } ?: "Untracked SKU",
        unit = guessUnitForItem(item),
        start = startQuantity,
        sold = soldQuantity,
        onHand = onHand,
        status = status,
        threshold = threshold
    )
}

private fun guessUnitForItem(item: ItemEntity): String {
    val name = item.name.lowercase(Locale.getDefault())
    return when {
        name.contains("brisket") || name.contains("rib") -> "lbs"
        name.contains("loaf") || name.contains("bread") -> "items"
        name.contains("cup") -> "cups"
        else -> "qty"
    }
}
