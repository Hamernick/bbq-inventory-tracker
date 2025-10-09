package com.bbqreset.domain

import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class ResetPlanner(
    private val clock: Clock
) {
    fun planNextRun(
        openHour: Int,
        openMinute: Int,
        zoneId: ZoneId
    ): ResetPlan {
        require(openHour in 0..23) { "openHour must be 0-23" }
        require(openMinute in 0..59) { "openMinute must be 0-59" }

        val nowInstant = clock.instant()
        val nowZoned = nowInstant.atZone(zoneId)
        val todayTarget = nowZoned.withHour(openHour).withMinute(openMinute)
            .withSecond(0).withNano(0)

        val scheduled = if (nowZoned.isBefore(todayTarget)) {
            todayTarget
        } else {
            todayTarget.plusDays(1)
        }

        val delay = Duration.between(nowInstant, scheduled.toInstant()).let {
            if (it.isNegative) Duration.ZERO else it
        }
        return ResetPlan(
            scheduledAtEpochSeconds = scheduled.toEpochSecond(),
            delay = delay,
            targetDate = scheduled.toLocalDate()
        )
    }
}

data class ResetPlan(
    val scheduledAtEpochSeconds: Long,
    val delay: Duration,
    val targetDate: LocalDate
)
