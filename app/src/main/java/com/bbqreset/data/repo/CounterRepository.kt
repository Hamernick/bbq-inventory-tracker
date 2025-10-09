package com.bbqreset.data.repo

import com.bbqreset.data.db.AppDatabase
import com.bbqreset.data.db.dao.CounterDao
import com.bbqreset.data.db.dao.TemplateDao
import com.bbqreset.data.db.dao.TemplateItemDao
import com.bbqreset.data.db.entity.CounterEntity
import java.time.LocalDate

class CounterRepository(
    private val counterDao: CounterDao,
    private val templateDao: TemplateDao,
    private val templateItemDao: TemplateItemDao
) {
    suspend fun applyTemplateForDate(
        templateId: Long,
        locationId: Long,
        targetDate: LocalDate
    ): ApplyTemplateResult {
        val template = templateDao.getTemplateWithItems(templateId)
            ?: return ApplyTemplateResult.TemplateNotFound
        val templateItems = templateItemDao.listForTemplate(templateId)
        if (templateItems.isEmpty()) {
            return ApplyTemplateResult.EmptyTemplate
        }
        val dateKey = targetDate.toString()
        val existing = counterDao.listCountersForDate(locationId, dateKey)
            .associateBy { it.itemId }
        if (existing.isNotEmpty()) {
            val allCovered = templateItems.all { templateItem ->
                val current = existing[templateItem.itemId]
                current != null && current.startQuantity == templateItem.startQuantity
            }
            if (allCovered && existing.keys.containsAll(templateItems.map { it.itemId })) {
                return ApplyTemplateResult.AlreadyApplied
            }
        }
        val counters = templateItems.map { templateItem ->
            val current = existing[templateItem.itemId]
            CounterEntity(
                date = dateKey,
                itemId = templateItem.itemId,
                locationId = locationId,
                startQuantity = templateItem.startQuantity,
                soldQuantity = current?.soldQuantity ?: 0,
                manualAdjustment = current?.manualAdjustment ?: 0,
                closedOnEpochSeconds = current?.closedOnEpochSeconds
            )
        }

        counterDao.upsertAll(counters)
        return ApplyTemplateResult.Success(
            templateId = template.template.id,
            templateName = template.template.name,
            appliedDate = dateKey,
            itemCount = counters.size
        )
    }

    companion object {
        fun fromDatabase(database: AppDatabase): CounterRepository {
            return CounterRepository(
                counterDao = database.counterDao(),
                templateDao = database.templateDao(),
                templateItemDao = database.templateItemDao()
            )
        }
    }
}

sealed interface ApplyTemplateResult {
    data class Success(
        val templateId: Long,
        val templateName: String,
        val appliedDate: String,
        val itemCount: Int
    ) : ApplyTemplateResult

    data object TemplateNotFound : ApplyTemplateResult

    data object EmptyTemplate : ApplyTemplateResult

    data object AlreadyApplied : ApplyTemplateResult
}
