package com.bbqreset.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bbqreset.core.di.DatabaseModule
import com.bbqreset.data.repo.ApplyQueueRepository
import com.bbqreset.data.db.seedDebug
import com.bbqreset.domain.usecase.EnqueueApplyUseCase
import com.bbqreset.domain.usecase.SetWeekQuantityUseCase
import com.bbqreset.data.repo.LogRepository
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DayKey { MON, TUE, WED, THU, FRI, SAT, SUN }

data class WeekItemUi(
    val itemId: Long,
    val name: String,
    val sku: String?,
    val unitType: String,
    val defaultQty: Int,
    val days: Map<DayKey, Int>
)

data class CatalogItemUi(val id: Long, val name: String, val unitType: String = "count")
data class LocationUi(val id: Long, val name: String)

data class WeekGridUiState(
    val connected: Boolean,
    val locationId: Long,
    val locationName: String,
    val locations: List<LocationUi>,
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val items: List<WeekItemUi>,
    val selectedIds: Set<Long>,
    val catalog: List<CatalogItemUi>,
    val isLoading: Boolean = false
) {
    val weekLabel: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
            return "${weekStart.format(formatter)} – ${weekEnd.format(formatter)}"
        }
}

sealed interface QuantityField {
    data object Default : QuantityField
    data class Day(val day: DayKey) : QuantityField
}

class WeekGridViewModel(app: Application) : AndroidViewModel(app) {
    private val clock = Clock.systemUTC()
    private val database by lazy { DatabaseModule.provideAppDatabase(app.applicationContext, clock) }
    private val applyRepo by lazy { ApplyQueueRepository(database.jobDao(), clock) }
    private val enqueue by lazy { EnqueueApplyUseCase(app.applicationContext, applyRepo) }
    private val planRepo by lazy { com.bbqreset.data.repo.WeekPlanRepository(database.weekPlanDao()) }
    private val loadPlan by lazy { com.bbqreset.domain.usecase.LoadWeekPlanUseCase(planRepo) }
    private val setQty by lazy { SetWeekQuantityUseCase(planRepo) }
    private val logRepo by lazy { LogRepository(database.logDao(), clock) }

    private val _ui = MutableStateFlow(seedState())
    val ui: StateFlow<WeekGridUiState> = _ui.asStateFlow()

    fun connect() {
        _ui.update { it.copy(connected = true) }
        refresh()
    }

    fun setWeekFromDate(date: LocalDate) {
        val start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        _ui.update { it.copy(weekStart = start, weekEnd = start.plusDays(6)) }
    }

    fun selectLocation(id: Long) {
        _ui.update { state ->
            val next = state.locations.firstOrNull { it.id == id } ?: return@update state
            state.copy(locationId = next.id, locationName = next.name)
        }
        refresh()
    }

    fun toggleSelection(id: Long) {
        _ui.update { state ->
            val next = state.selectedIds.toMutableSet()
            if (next.contains(id)) next.remove(id) else next.add(id)
            state.copy(selectedIds = next)
        }
    }

    fun addItems(ids: List<Long>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            val state = _ui.value
            val existing = state.items.associateBy { it.itemId }
            val toAdd = state.catalog.filter { ids.contains(it.id) && !existing.containsKey(it.id) }
            if (toAdd.isEmpty()) return@launch
            val startId = (database.itemDao().maxId() ?: 0L) + 1
            toAdd.forEachIndexed { idx, catalogItem ->
                val itemId = startId + idx
                database.itemDao().upsert(
                    com.bbqreset.data.db.entity.ItemEntity(
                        id = itemId,
                        cloverItemId = catalogItem.id.toString(),
                        name = catalogItem.name,
                        sku = null,
                        locationId = state.locationId,
                        unitType = catalogItem.unitType
                    )
                )
                setQty(state.locationId, state.weekStart.toString(), itemId, 0, null)
            }
            refresh()
        }
    }

    fun createItem(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            val state = _ui.value
            val newId = (database.itemDao().maxId() ?: 0L) + 1
            database.itemDao().upsert(
                com.bbqreset.data.db.entity.ItemEntity(
                    id = newId,
                    cloverItemId = null,
                    name = trimmed,
                    sku = null,
                    locationId = state.locationId,
                    unitType = "count"
                )
            )
            setQty(state.locationId, state.weekStart.toString(), newId, 0, null)
            refresh()
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val state = _ui.value
            if (state.selectedIds.isEmpty()) return@launch
            val ids = state.selectedIds.toList()
            planRepo.deleteItems(state.locationId, ids, state.weekStart.toString())
            database.itemDao().deleteByIds(ids)
            refresh()
        }
    }

    fun updateQuantity(itemId: Long, field: QuantityField, quantity: Int) {
        viewModelScope.launch {
            val state = _ui.value
            val prior = state.items.firstOrNull { it.itemId == itemId }
            val capped = quantity.coerceAtLeast(0)
            val dayKey = when (field) {
                is QuantityField.Default -> null
                is QuantityField.Day -> field.day
            }
            setQty(state.locationId, state.weekStart.toString(), itemId, capped, dayKey)
            logRepo.record(
                actor = "local",
                action = "set_quantity",
                meta = mapOf(
                    "locationId" to state.locationId,
                    "weekStart" to state.weekStart.toString(),
                    "itemId" to itemId,
                    "field" to (dayKey?.name ?: "default"),
                    "from" to when (field) {
                        is QuantityField.Default -> prior?.defaultQty
                        is QuantityField.Day -> prior?.days?.get(field.day)
                    },
                    "to" to capped,
                    "unitType" to (prior?.unitType ?: "")
                )
            )
            refresh()
        }
    }

    fun applyNow() {
        val state = _ui.value
        viewModelScope.launch { enqueue(state.locationId, state.weekStart.toString()) }
    }

    fun reseedSample() {
        viewModelScope.launch {
            database.seedDebug(clock)
            refresh()
        }
    }

    private fun seedState(): WeekGridUiState {
        val start = currentWeekStart()
        val catalog = listOf(
            CatalogItemUi(4, "Chicken Wings", "count"),
            CatalogItemUi(5, "Sausage Links", "count"),
            CatalogItemUi(6, "Cornbread Muffins", "each"),
            CatalogItemUi(7, "Mac & Cheese", "pan")
        )
        val locations = listOf(
            LocationUi(1, "Downtown BBQ"),
            LocationUi(2, "Uptown BBQ"),
            LocationUi(3, "Bakehouse North")
        )
        return WeekGridUiState(
            connected = false,
            locationId = locations.first().id,
            locationName = locations.first().name,
            locations = locations,
            weekStart = start,
            weekEnd = start.plusDays(6),
            items = emptyList(),
            selectedIds = emptySet(),
            catalog = catalog
        )
    }

    fun refresh() {
        viewModelScope.launch {
            val state = _ui.value
            _ui.update { it.copy(isLoading = true) }
            val rows = loadPlan(state.locationId, state.weekStart.toString())
            val items = rows.map { row ->
                WeekItemUi(
                    itemId = row.itemId,
                    name = row.name,
                    sku = row.sku,
                    unitType = row.unitType.ifBlank { "count" },
                    defaultQty = row.quantityDefault,
                    days = fullDayMap(
                        mapOf(
                            DayKey.MON to row.quantityMon,
                            DayKey.TUE to row.quantityTue,
                            DayKey.WED to row.quantityWed,
                            DayKey.THU to row.quantityThu,
                            DayKey.FRI to row.quantityFri,
                            DayKey.SAT to row.quantitySat,
                            DayKey.SUN to row.quantitySun
                        )
                    )
                )
            }
            val locName = database.locationDao().getLocationById(state.locationId)?.name ?: state.locationName
            _ui.update { it.copy(items = items, locationName = locName, isLoading = false) }
        }
    }

    private fun currentWeekStart(): LocalDate =
        LocalDate.now(clock).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun emptyDayMap(): Map<DayKey, Int> = DayKey.entries.associateWith { 0 }

    private fun fullDayMap(values: Map<DayKey, Int>): Map<DayKey, Int> =
        DayKey.entries.associateWith { day -> values[day] ?: 0 }
}
