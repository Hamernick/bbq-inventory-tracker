package com.bbqreset.domain.usecase

import com.bbqreset.data.db.dao.TemplateDao
import com.bbqreset.data.repo.WeekPlanRepository

class PrefillWeekPlanFromTemplateUseCase(
    private val repo: WeekPlanRepository,
    private val templateDao: TemplateDao
) {
    suspend operator fun invoke(locationId: Long, weekStart: String, templateId: Long? = null): Boolean {
        val template = if (templateId != null) {
            templateDao.getTemplateWithItems(templateId)
        } else {
            templateDao.listTemplatesWithItems(locationId).firstOrNull()
        } ?: return false

        for (item in template.items) {
            // Only apply for this location's items
            if (item.item.locationId == locationId) {
                repo.setQuantity(locationId, weekStart, item.item.id, item.templateItem.startQuantity, null)
            }
        }
        return true
    }
}
