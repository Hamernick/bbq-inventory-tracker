package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbqreset.data.db.entity.JobEntity
import com.bbqreset.data.db.entity.JobStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs WHERE id = :jobId")
    suspend fun getJob(jobId: Long): JobEntity?

    @Query("SELECT * FROM jobs WHERE dedupe_key = :dedupeKey LIMIT 1")
    suspend fun getJobByDedupeKey(dedupeKey: String): JobEntity?

    @Query("SELECT * FROM jobs WHERE kind = :kind ORDER BY scheduled_for")
    fun observeJobsForKind(kind: String): Flow<List<JobEntity>>

    @Query(
        """
        SELECT * FROM jobs 
        WHERE status IN ('PENDING', 'RUNNING')
        ORDER BY scheduled_for ASC
        """
    )
    suspend fun listActiveJobs(): List<JobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(job: JobEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(jobs: List<JobEntity>)

    @Query(
        """
        UPDATE jobs 
        SET status = :status, last_error = :lastError 
        WHERE id = :jobId
        """
    )
    suspend fun updateStatus(jobId: Long, status: JobStatus, lastError: String?)

    @Query("DELETE FROM jobs WHERE status = :status")
    suspend fun deleteByStatus(status: JobStatus)
}
