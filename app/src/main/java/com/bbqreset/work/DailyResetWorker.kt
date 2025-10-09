package com.bbqreset.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bbqreset.core.di.DatabaseModule
import com.bbqreset.data.repo.CounterRepository
import com.bbqreset.data.repo.JobRepository
import com.bbqreset.data.repo.LogRepository
import java.time.Clock
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyResetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val clock: Clock = Clock.systemUTC()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val locationId = inputData.getLong(KEY_LOCATION_ID, -1L)
        val jobId = inputData.getLong(KEY_JOB_ID, -1L)
        val dateIso = inputData.getString(KEY_TARGET_DATE)

        if (locationId <= 0 || jobId <= 0 || dateIso.isNullOrBlank()) {
            return@withContext Result.failure()
        }

        val targetDate = runCatching { LocalDate.parse(dateIso) }.getOrElse {
            return@withContext Result.failure()
        }

        val database = DatabaseModule.provideAppDatabase(applicationContext, clock = clock)
        val jobRepository = JobRepository(database.jobDao(), clock)
        val counterRepository = CounterRepository.fromDatabase(database)
        val logRepository = LogRepository(database.logDao(), clock)

        val templateId = findDefaultTemplateId(database.templateDao().listTemplates(locationId))
        if (templateId <= 0) {
            jobRepository.markErrored(jobId, "No template configured")
            logRepository.record(
                actor = "worker",
                action = "daily_reset_failed",
                meta = mapOf(
                    "locationId" to locationId,
                    "reason" to "no_template_configured",
                    "date" to targetDate.toString()
                )
            )
            return@withContext Result.failure()
        }

        jobRepository.markRunning(jobId)

        return@withContext when (val result = counterRepository.applyTemplateForDate(
            templateId = templateId,
            locationId = locationId,
            targetDate = targetDate
        )) {
            is com.bbqreset.data.repo.ApplyTemplateResult.Success -> {
                jobRepository.markCompleted(jobId)
                logRepository.record(
                    actor = "worker",
                    action = "daily_reset_applied",
                    meta = mapOf(
                        "templateId" to result.templateId,
                        "templateName" to result.templateName,
                        "locationId" to locationId,
                        "date" to result.appliedDate,
                        "items" to result.itemCount
                    )
                )
                Result.success()
            }
            com.bbqreset.data.repo.ApplyTemplateResult.AlreadyApplied -> {
                jobRepository.markCompleted(jobId)
                Result.success(
                    workDataOf(
                        KEY_SKIPPED to true
                    )
                )
            }
            com.bbqreset.data.repo.ApplyTemplateResult.TemplateNotFound -> {
                jobRepository.markErrored(jobId, "Template not found")
                logRepository.record(
                    actor = "worker",
                    action = "daily_reset_failed",
                    meta = mapOf(
                        "locationId" to locationId,
                        "reason" to "template_not_found",
                        "date" to targetDate.toString()
                    )
                )
                Result.failure()
            }
            com.bbqreset.data.repo.ApplyTemplateResult.EmptyTemplate -> {
                jobRepository.markErrored(jobId, "Template empty")
                logRepository.record(
                    actor = "worker",
                    action = "daily_reset_failed",
                    meta = mapOf(
                        "locationId" to locationId,
                        "reason" to "template_empty",
                        "date" to targetDate.toString()
                    )
                )
                Result.retry()
            }
        }
    }

    private fun findDefaultTemplateId(templates: List<com.bbqreset.data.db.entity.TemplateEntity>): Long {
        return templates.firstOrNull()?.id ?: -1L
    }

    companion object {
        const val KEY_LOCATION_ID = "location_id"
        const val KEY_TARGET_DATE = "target_date"
        const val KEY_JOB_ID = "job_id"
        const val KEY_SKIPPED = "skipped"
        const val UNIQUE_WORK_PREFIX = "daily_reset_"
    }
}
