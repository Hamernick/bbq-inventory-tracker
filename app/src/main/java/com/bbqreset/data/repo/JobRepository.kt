package com.bbqreset.data.repo

import com.bbqreset.data.db.dao.JobDao
import com.bbqreset.data.db.entity.JobEntity
import com.bbqreset.data.db.entity.JobStatus
import java.time.Clock

class JobRepository(
    private val jobDao: JobDao,
    private val clock: Clock
) {

    suspend fun findByDedupeKey(dedupeKey: String): JobEntity? {
        return jobDao.getJobByDedupeKey(dedupeKey)
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

    companion object
}
