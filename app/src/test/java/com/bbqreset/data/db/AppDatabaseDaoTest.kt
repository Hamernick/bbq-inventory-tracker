package com.bbqreset.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.bbqreset.data.db.entity.JobStatus
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
        val counters = database.counterDao().listCountersForDate(
            locations.first().id,
            mostRecentDate!!
        )
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

    // Counter/template apply tests removed per AGENTS: counter math replaced by direct planning
}
