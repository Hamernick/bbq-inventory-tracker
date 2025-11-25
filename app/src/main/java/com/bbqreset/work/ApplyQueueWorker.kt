@file:Suppress(
    "TooGenericExceptionCaught",
    "LongMethod",
    "CyclomaticComplexMethod",
    "NestedBlockDepth",
    "LoopWithTooManyJumpStatements",
    "MagicNumber"
)

package com.bbqreset.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bbqreset.core.di.DatabaseModule
import com.bbqreset.data.api.ApiClientProvider
import com.bbqreset.data.api.ApiConfig
import com.bbqreset.data.api.MerchantIdProvider
import com.bbqreset.data.api.SecureMerchantIdProvider
import com.bbqreset.data.api.SecureTokenProvider
import com.bbqreset.data.api.TokenProvider
import com.bbqreset.data.api.dto.StockUpdateRequest
import com.bbqreset.data.db.entity.JobStatus
import com.bbqreset.data.repo.ApplyQueueRepository
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.delay
import retrofit2.HttpException

class ApplyQueueWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val clock = Clock.systemUTC()
        val db = DatabaseModule.provideAppDatabase(applicationContext, clock)
        val jobDao = db.jobDao()
        val itemDao = db.itemDao()
        val weekDao = db.weekPlanDao()
        val active = jobDao.listActiveJobs()
        val applyJobs = active.filter { it.kind == ApplyQueueRepository.KIND_APPLY }

        val tokenProvider: TokenProvider = SecureTokenProvider(applicationContext)
        val merchantIdProvider: MerchantIdProvider = SecureMerchantIdProvider(applicationContext)
        val merchantId = merchantIdProvider.getMerchantId()
        val token = tokenProvider.getToken()
        val inventoryApi = ApiClientProvider.inventoryApi(
            baseUrl = ApiConfig.getBaseUrl(applicationContext),
            tokenProvider = tokenProvider,
            logging = false
        )

        for (job in applyJobs) {
            try {
                jobDao.updateStatus(job.id, JobStatus.RUNNING, null)
                val key = job.dedupeKey
                if (key.isNullOrBlank()) {
                    jobDao.updateStatus(job.id, JobStatus.ERROR, "Missing dedupe key")
                    continue
                }
                val parts = key.split("_", limit = 3)
                if (parts.size < 3) {
                    jobDao.updateStatus(job.id, JobStatus.ERROR, "Bad dedupe key format")
                    continue
                }
                val locationId = parts[1].toLongOrNull()
                val weekStart = parts[2]
                if (locationId == null) {
                    jobDao.updateStatus(job.id, JobStatus.ERROR, "Invalid location id in key")
                    continue
                }

                if (merchantId.isNullOrBlank() || token.isNullOrBlank()) {
                    jobDao.updateStatus(job.id, JobStatus.ERROR, "Missing merchant or token")
                    continue
                }

                val rows = weekDao.listPlanRows(locationId, weekStart)
                val applyDay = DayOfWeek.MONDAY
                for (row in rows) {
                    val item = itemDao.getItemById(row.itemId)
                    val cloverId = item?.cloverItemId
                    if (cloverId.isNullOrBlank()) continue
                    val qty = when (applyDay) {
                        DayOfWeek.MONDAY -> row.quantityMon.takeIf { it > 0 } ?: row.quantityDefault
                        DayOfWeek.TUESDAY -> row.quantityTue.takeIf { it > 0 } ?: row.quantityDefault
                        DayOfWeek.WEDNESDAY -> row.quantityWed.takeIf { it > 0 } ?: row.quantityDefault
                        DayOfWeek.THURSDAY -> row.quantityThu.takeIf { it > 0 } ?: row.quantityDefault
                        DayOfWeek.FRIDAY -> row.quantityFri.takeIf { it > 0 } ?: row.quantityDefault
                        DayOfWeek.SATURDAY -> row.quantitySat.takeIf { it > 0 } ?: row.quantityDefault
                        DayOfWeek.SUNDAY -> row.quantitySun.takeIf { it > 0 } ?: row.quantityDefault
                    }
                    if (qty <= 0) continue
                    val idempotencyKey = "apply-${'$'}locationId-${'$'}weekStart-${'$'}cloverId"
                    val maxAttempts = 3
                    var attempt = 0
                    while (attempt < maxAttempts) {
                        attempt++
                        try {
                            inventoryApi.updateStock(
                                merchantId = merchantId,
                                itemId = cloverId,
                                body = StockUpdateRequest(quantity = qty),
                                idempotencyKey = idempotencyKey
                            )
                            break
                        } catch (t: Throwable) {
                            if (!isRetryable(t, attempt, maxAttempts)) {
                                jobDao.updateStatus(job.id, JobStatus.ERROR, t.message)
                                break
                            }
                            delay(backoffMillis(attempt))
                        }
                    }
                }
                jobDao.updateStatus(job.id, JobStatus.DONE, null)
            } catch (t: Throwable) {
                jobDao.updateStatus(job.id, JobStatus.ERROR, t.message)
            }
        }
        return Result.success()
    }

    private fun isRetryable(t: Throwable, attempt: Int, maxAttempts: Int): Boolean {
        val code = (t as? HttpException)?.code()
        return when (code) {
            401, 429 -> attempt < maxAttempts
            in 500..599 -> attempt < maxAttempts
            else -> false
        }
    }

    private fun backoffMillis(attempt: Int): Long = (attempt * 1000L).coerceAtMost(5000L)
}
