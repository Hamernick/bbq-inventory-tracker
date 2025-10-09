package com.bbqreset.data.repo

import com.bbqreset.data.db.dao.JobDao
import com.bbqreset.data.db.entity.JobEntity
import com.bbqreset.data.db.entity.JobStatus
import java.time.Clock
import java.time.LocalDate

class JobRepository(
    private val jobDao: JobDao,
    private val clock: Clock
    ) {

    suspend fun findByDedupeKey(dedupeKey: String): JobEntity? {
        return jobDao.getJobByDedupeKey(dedupeKey)
    }

    suspend fun ensurePendingDailyResetJob(
        locationId: Long,
        targetDate: LocalDate,
        scheduledForEpochSeconds: Long
    ): JobEntity {
        val dedupeKey = dailyResetDedupeKey(locationId, targetDate)
        val existing = jobDao.getJobByDedupeKey(dedupeKey)
        if (existing != null && existing.status == JobStatus.DONE) {
            return existing
        }

        val job = if (existing != null) {
            val resetStatus = if (existing.status == JobStatus.ERROR) JobStatus.PENDING else existing.status
            existing.copy(
                scheduledForEpochSeconds = scheduledForEpochSeconds,
                status = resetStatus,
                lastError = if (resetStatus == JobStatus.PENDING) null else existing.lastError
            )
        } else {
            JobEntity(
                kind = DAILY_RESET_KIND,
                scheduledForEpochSeconds = scheduledForEpochSeconds,
                status = JobStatus.PENDING,
                lastError = null,
                dedupeKey = dedupeKey
            )
        }

        val id = jobDao.upsert(job)
        val resolvedId = if (job.id != 0L) job.id else id
        return job.copy(id = resolvedId)
    }

    suspend fun markRunning(jobId: Long) {
        jobDao.updateStatus(jobId, JobStatus.RUNNING, null)
    }

    suspend fun markCompleted(jobId: Long) {
        jobDao.updateStatus(jobId, JobStatus.DONE, null)
    }

    suspend fun markErrored(jobId: Long, message: String?) {
        jobDao.updateStatus(jobId, JobStatus.ERROR, message)
    }

    suspend fun listActiveJobs(): List<JobEntity> = jobDao.listActiveJobs()

    companion object {
        const val DAILY_RESET_KIND = "daily_reset"

        fun dailyResetDedupeKey(locationId: Long, date: LocalDate): String =
            "daily_reset_${locationId}_${date}"
    }
}
