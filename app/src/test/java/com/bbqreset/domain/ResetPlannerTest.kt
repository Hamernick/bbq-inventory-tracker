package com.bbqreset.domain

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class ResetPlannerTest {
    private val zoneId = ZoneId.of("America/Chicago")

    @Test
    fun schedulesForSameDayWhenBeforeOpen() {
        val clock = Clock.fixed(Instant.parse("2025-10-08T08:00:00Z"), ZoneOffset.UTC)
        val planner = ResetPlanner(clock)

        val plan = planner.planNextRun(openHour = 5, openMinute = 0, zoneId = zoneId)

        assertEquals("2025-10-08", plan.targetDate.toString())
        assertTrue(plan.delay.toMinutes() >= 0)
        assertEquals(Instant.parse("2025-10-08T10:00:00Z").epochSecond, plan.scheduledAtEpochSeconds)
    }

    @Test
    fun schedulesForNextDayWhenPastOpen() {
        val clock = Clock.fixed(Instant.parse("2025-10-08T15:00:00Z"), ZoneOffset.UTC)
        val planner = ResetPlanner(clock)

        val plan = planner.planNextRun(openHour = 4, openMinute = 30, zoneId = zoneId)

        assertEquals("2025-10-09", plan.targetDate.toString())
        assertEquals(Instant.parse("2025-10-09T09:30:00Z").epochSecond, plan.scheduledAtEpochSeconds)
    }
}
