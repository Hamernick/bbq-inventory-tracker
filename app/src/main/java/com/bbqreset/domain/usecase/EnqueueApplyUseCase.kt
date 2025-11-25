package com.bbqreset.domain.usecase

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bbqreset.data.repo.ApplyQueueRepository
import com.bbqreset.work.ApplyQueueWorker

class EnqueueApplyUseCase(
    private val context: Context,
    private val applyRepo: ApplyQueueRepository
) {
    suspend operator fun invoke(locationId: Long, weekStartIso: String) {
        applyRepo.ensurePendingApply(locationId, weekStartIso)
        val wm = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<ApplyQueueWorker>().build()
        wm.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.KEEP, request)
    }

    companion object { const val WORK_NAME = "apply-queue" }
}
