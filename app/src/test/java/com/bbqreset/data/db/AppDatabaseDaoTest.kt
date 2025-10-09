package com.bbqreset.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bbqreset.data.db.entity.JobStatus
import com.bbqreset.data.repo.CounterRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AppDatabaseDaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun debugSeeder_populatesAllCoreTables() = runTest {
        val clock = Clock.fixed(Instant.parse("2025-10-08T12:00:00Z"), ZoneOffset.UTC)
        database.seedDebug(clock)

        val locations = database.locationDao().listLocations()
        val items = database.itemDao().listItemsForLocation(locations.first().id)
        val template = database.templateDao().listTemplates(locations.first().id).first()
        val templateItems = database.templateItemDao().listForTemplate(template.id)
        val mostRecentDate = database.counterDao().getMostRecentDate(locations.first().id)
        val counters = database.counterDao().listCountersForDate(locations.first().id, mostRecentDate!!)
        val jobs = database.jobDao().listActiveJobs()
        val logs = database.logDao().pageLogs(limit = 10, offset = 0)

        assertEquals(1, locations.size)
        assertEquals(3, items.size)
        assertEquals(3, templateItems.size)
        assertEquals(3, counters.size)
        assertTrue(jobs.isNotEmpty())
        assertTrue(logs.isNotEmpty())
        assertEquals("2025-10-08", mostRecentDate)
    }

    @Test
    fun templateRelation_returnsItems() = runTest {
        val clock = Clock.fixed(Instant.parse("2025-10-08T12:00:00Z"), ZoneOffset.UTC)
        database.seedDebug(clock)

        val locationId = database.locationDao().listLocations().first().id
        val template = database.templateDao().listTemplates(locationId).first()
        val templateWithItems = database.templateDao().getTemplateWithItems(template.id)

        assertNotNull(templateWithItems)
        assertEquals(3, templateWithItems.items.size)
        assertTrue(templateWithItems.items.all { it.item.name.isNotBlank() })
    }

    @Test
    fun jobDao_updatesStatusAndPersists() = runTest {
        val clock = Clock.fixed(Instant.parse("2025-10-08T12:00:00Z"), ZoneOffset.UTC)
        database.seedDebug(clock)

        val job = database.jobDao().listActiveJobs().first()
        database.jobDao().updateStatus(job.id, JobStatus.DONE, lastError = null)

        val persisted = database.jobDao().getJob(job.id)
        assertNotNull(persisted)
        assertEquals(JobStatus.DONE, persisted.status)
    }

    @Test
    fun applyTemplate_populatesCountersForTargetDate() = runTest {
        val clock = Clock.fixed(Instant.parse("2025-10-08T12:00:00Z"), ZoneOffset.UTC)
        database.seedDebug(clock)

        val counterRepository = CounterRepository.fromDatabase(database)
        val locationId = database.locationDao().listLocations().first().id
        val templateId = database.templateDao().listTemplates(locationId).first().id

        val targetDate = LocalDate.parse("2025-10-09")
        val result = counterRepository.applyTemplateForDate(
            templateId = templateId,
            locationId = locationId,
            targetDate = targetDate
        )

        val success = result as? com.bbqreset.data.repo.ApplyTemplateResult.Success
        assertNotNull(success)
        assertEquals(templateId, success.templateId)
        val counters = database.counterDao().listCountersForDate(locationId, targetDate.toString())
        assertEquals(3, counters.size)
        val brisket = counters.first { it.itemId == 1L }
        assertEquals(48, brisket.startQuantity)
    }

    @Test
    fun applyTemplate_isIdempotent() = runTest {
        val clock = Clock.fixed(Instant.parse("2025-10-08T12:00:00Z"), ZoneOffset.UTC)
        database.seedDebug(clock)

        val counterRepository = CounterRepository.fromDatabase(database)
        val locationId = database.locationDao().listLocations().first().id
        val templateId = database.templateDao().listTemplates(locationId).first().id

        val targetDate = LocalDate.parse("2025-10-08")
        database.counterDao().deleteForDate(locationId, targetDate.toString())
        val first = counterRepository.applyTemplateForDate(
            templateId = templateId,
            locationId = locationId,
            targetDate = targetDate
        )
        val second = counterRepository.applyTemplateForDate(
            templateId = templateId,
            locationId = locationId,
            targetDate = targetDate
        )

        assertTrue(first is com.bbqreset.data.repo.ApplyTemplateResult.Success)
        assertTrue(second is com.bbqreset.data.repo.ApplyTemplateResult.AlreadyApplied)
    }
}

