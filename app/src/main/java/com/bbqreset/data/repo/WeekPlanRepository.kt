package com.bbqreset.data.repo

import com.bbqreset.ui.vm.DayKey
import com.bbqreset.data.db.dao.WeekPlanDao
import com.bbqreset.data.db.dao.WeekPlanRow
import com.bbqreset.data.db.entity.WeekPlanEntity

class WeekPlanRepository(private val dao: WeekPlanDao) {
    suspend fun list(locationId: Long, weekStart: String): List<WeekPlanRow> =
        dao.listPlanRows(locationId, weekStart)

    suspend fun setQuantity(
        locationId: Long,
        weekStart: String,
        itemId: Long,
        quantity: Int,
        day: DayKey?
    ) {
        if (quantity < 0) return
        ensureRow(locationId, weekStart, itemId)
        when (day) {
            null -> dao.updateDefaultQuantity(weekStart, itemId, locationId, quantity)
            DayKey.MON -> dao.updateMon(weekStart, itemId, locationId, quantity)
            DayKey.TUE -> dao.updateTue(weekStart, itemId, locationId, quantity)
            DayKey.WED -> dao.updateWed(weekStart, itemId, locationId, quantity)
            DayKey.THU -> dao.updateThu(weekStart, itemId, locationId, quantity)
            DayKey.FRI -> dao.updateFri(weekStart, itemId, locationId, quantity)
            DayKey.SAT -> dao.updateSat(weekStart, itemId, locationId, quantity)
            DayKey.SUN -> dao.updateSun(weekStart, itemId, locationId, quantity)
        }
    }

    suspend fun clear(locationId: Long, weekStart: String) {
        dao.clearWeek(weekStart, locationId)
    }

    suspend fun deleteItems(locationId: Long, itemIds: List<Long>, weekStart: String) {
        dao.deleteForItems(itemIds, locationId)
    }

    private suspend fun ensureRow(locationId: Long, weekStart: String, itemId: Long) {
        dao.upsert(
            WeekPlanEntity(
                weekStart = weekStart,
                itemId = itemId,
                locationId = locationId
            )
        )
    }
}
