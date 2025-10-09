package com.bbqreset.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bbqreset.data.repo.JobRepository
import com.bbqreset.domain.ResetPlan
import com.bbqreset.domain.ResetPlanner
import com.bbqreset.data.db.entity.JobStatus
import java.time.ZoneId

class DailyResetScheduler(
    private val workManager: WorkManager,
    private val jobRepository: JobRepository,
    private val resetPlanner: ResetPlanner
) {

    suspend fun schedule(
        locationId: Long,
        openHour: Int,
        openMinute: Int,
        zoneId: ZoneId
    ): ResetPlan? {
        val plan = resetPlanner.planNextRun(openHour, openMinute, zoneId)
        val dedupeKey = JobRepository.dailyResetDedupeKey(locationId, plan.targetDate)
        val existingJob = jobRepository.findByDedupeKey(dedupeKey)
        if (existingJob != null && existingJob.status == JobStatus.DONE) {
            return null
        }

        val job = jobRepository.ensurePendingDailyResetJob(
            locationId = locationId,
            targetDate = plan.targetDate,
            scheduledForEpochSeconds = plan.scheduledAtEpochSeconds
        )

        val workName = DailyResetWorker.UNIQUE_WORK_PREFIX + locationId
        val workRequest = OneTimeWorkRequestBuilder<DailyResetWorker>()
            .setInitialDelay(plan.delay)
            .setInputData(
                workDataOf(
                    DailyResetWorker.KEY_LOCATION_ID to locationId,
                    DailyResetWorker.KEY_TARGET_DATE to plan.targetDate.toString(),
                    DailyResetWorker.KEY_JOB_ID to job.id
                )
            )
            .addTag(WORK_TAG)
            .build()

        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        return plan
    }

    companion object {
        const val WORK_TAG = "daily_reset_tag"

        fun create(context: Context, jobRepository: JobRepository, resetPlanner: ResetPlanner): DailyResetScheduler {
            return DailyResetScheduler(
                workManager = WorkManager.getInstance(context.applicationContext),
                jobRepository = jobRepository,
                resetPlanner = resetPlanner
            )
        }
    }
}
