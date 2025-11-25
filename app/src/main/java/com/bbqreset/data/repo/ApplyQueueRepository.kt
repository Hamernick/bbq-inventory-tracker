package com.bbqreset.data.repo

import com.bbqreset.data.db.dao.JobDao
import com.bbqreset.data.db.entity.JobEntity
import com.bbqreset.data.db.entity.JobStatus
import java.time.Clock
import java.time.Instant

class ApplyQueueRepository(
    private val jobDao: JobDao,
    private val clock: Clock
) {
    suspend fun ensurePendingApply(locationId: Long, weekStartIso: String): JobEntity {
        val dedupeKey = dedupeKey(locationId, weekStartIso)
        val existing = jobDao.getJobByDedupeKey(dedupeKey)
        if (existing != null && existing.status == JobStatus.DONE) return existing

        val now = Instant.now(clock).epochSecond
        val pending = if (existing != null) {
            val status = if (existing.status == JobStatus.ERROR) JobStatus.PENDING else existing.status
            existing.copy(
                scheduledForEpochSeconds = now,
                status = status,
                lastError = if (status == JobStatus.PENDING) null else existing.lastError
            )
        } else {
            JobEntity(
                kind = KIND_APPLY,
                scheduledForEpochSeconds = now,
                status = JobStatus.PENDING,
                lastError = null,
                dedupeKey = dedupeKey
            )
        }
        val id = jobDao.upsert(pending)
        val resolvedId = if (pending.id != 0L) pending.id else id
        return pending.copy(id = resolvedId)
    }

    companion object {
        const val KIND_APPLY = "apply"
        fun dedupeKey(locationId: Long, weekStartIso: String): String = "apply_" + locationId + "_" + weekStartIso
    }
}
