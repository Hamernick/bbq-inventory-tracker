package com.bbqreset.data.db

import androidx.room.withTransaction
import com.bbqreset.data.db.entity.CounterEntity
import com.bbqreset.data.db.entity.ItemEntity
import com.bbqreset.data.db.entity.JobEntity
import com.bbqreset.data.db.entity.JobStatus
import com.bbqreset.data.db.entity.LocationEntity
import com.bbqreset.data.db.entity.LogEntity
import com.bbqreset.data.db.entity.TemplateEntity
import com.bbqreset.data.db.entity.TemplateItemEntity
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DebugDatabaseSeeder(
    private val clock: Clock
) {
    suspend fun seed(database: AppDatabase) {
        val seedInstant = Instant.now(clock)
        val todayIso = LocalDate.now(clock).toString()

        val location = LocationEntity(
            id = 1L,
            name = "Downtown BBQ",
            timeZoneId = "America/Chicago"
        )

        val items = listOf(
            ItemEntity(
                id = 1L,
                cloverItemId = "clover-smoked-brisket",
                name = "Smoked Brisket",
                sku = "BBQ-001",
                locationId = location.id,
                unitType = "lbs"
            ),
            ItemEntity(
                id = 2L,
                cloverItemId = "clover-ribs-half",
                name = "Spare Ribs Half Rack",
                sku = "BBQ-004",
                locationId = location.id,
                unitType = "count"
            ),
            ItemEntity(
                id = 3L,
                cloverItemId = "clover-cornbread",
                name = "Cornbread Loaf",
                sku = "BAK-102",
                locationId = location.id,
                unitType = "each"
            )
        )

        val template = TemplateEntity(
            id = 1L,
            name = "Weekday Open",
            locationId = location.id,
            holidayCode = null
        )

        val templateItems = listOf(
            TemplateItemEntity(
                templateId = template.id,
                itemId = 1L,
                startQuantity = 48
            ),
            TemplateItemEntity(
                templateId = template.id,
                itemId = 2L,
                startQuantity = 36
            ),
            TemplateItemEntity(
                templateId = template.id,
                itemId = 3L,
                startQuantity = 52
            )
        )

        val counters = listOf(
            CounterEntity(
                date = todayIso,
                itemId = 1L,
                locationId = location.id,
                startQuantity = 48,
                soldQuantity = 22,
                manualAdjustment = 0,
                closedOnEpochSeconds = null
            ),
            CounterEntity(
                date = todayIso,
                itemId = 2L,
                locationId = location.id,
                startQuantity = 36,
                soldQuantity = 28,
                manualAdjustment = -2,
                closedOnEpochSeconds = null
            ),
            CounterEntity(
                date = todayIso,
                itemId = 3L,
                locationId = location.id,
                startQuantity = 52,
                soldQuantity = 12,
                manualAdjustment = 0,
                closedOnEpochSeconds = null
            )
        )

        val logs = listOf(
            LogEntity(
                ts = seedInstant.epochSecond,
                actor = "system",
                action = "seed_debug_data",
                metaJson = """{"locationId":${location.id}}"""
            ),
            LogEntity(
                ts = seedInstant.minusSeconds(3600).epochSecond,
                actor = "system",
                action = "daily_reset_completed",
                metaJson = """{"locationId":${location.id},"templateId":${template.id}}"""
            )
        )

        val jobs = listOf(
            JobEntity(
                kind = "daily_reset",
                scheduledForEpochSeconds = seedInstant.plusSeconds(3600).epochSecond,
                status = JobStatus.PENDING,
                lastError = null,
                dedupeKey = "daily_reset_${todayIso}_${location.id}"
            ),
            JobEntity(
                kind = "export_csv",
                scheduledForEpochSeconds = seedInstant.plusSeconds(7200).epochSecond,
                status = JobStatus.DONE,
                lastError = null,
                dedupeKey = "export_${todayIso}_${location.id}"
            )
        )

        database.withTransaction {
            database.locationDao().upsert(location)
            database.itemDao().upsertAll(items)
            database.templateDao().upsert(template)
            database.templateItemDao().upsertAll(templateItems)
            database.counterDao().upsertAll(counters)
            database.logDao().insertAll(logs)
            database.jobDao().upsertAll(jobs)
            // Seed a week plan for current week (Monday start) with template quantities
            val weekStart = java.time.LocalDate.now(clock)
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                .toString()
            val planDao = database.weekPlanDao()
            templateItems.forEach { ti ->
                planDao.upsert(
                    com.bbqreset.data.db.entity.WeekPlanEntity(
                        weekStart = weekStart,
                        itemId = ti.itemId,
                        locationId = location.id,
                        quantityDefault = ti.startQuantity,
                        quantityMon = ti.startQuantity,
                        quantityTue = ti.startQuantity,
                        quantityWed = ti.startQuantity,
                        quantityThu = ti.startQuantity,
                        quantityFri = ti.startQuantity,
                        quantitySat = ti.startQuantity,
                        quantitySun = ti.startQuantity
                    )
                )
            }
        }
    }
}

fun AppDatabase.seedDebug(clock: Clock = Clock.systemUTC()) {
    runBlocking {
        withContext(Dispatchers.IO) {
            DebugDatabaseSeeder(clock).seed(this@seedDebug)
        }
    }
}
