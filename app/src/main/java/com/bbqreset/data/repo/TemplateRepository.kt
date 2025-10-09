package com.bbqreset.data.repo

import com.bbqreset.data.db.dao.ItemDao
import com.bbqreset.data.db.dao.TemplateDao
import com.bbqreset.data.db.dao.TemplateItemDao
import com.bbqreset.data.db.entity.TemplateEntity
import com.bbqreset.data.db.entity.TemplateItemEntity
import com.bbqreset.data.db.model.TemplateItemWithItem
import com.bbqreset.data.db.model.TemplateWithItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TemplateRepository(
    private val templateDao: TemplateDao,
    private val templateItemDao: TemplateItemDao,
    private val itemDao: ItemDao
) {
    fun observeTemplates(locationId: Long): Flow<List<TemplateDetail>> {
        return templateDao.observeTemplatesWithItems(locationId).map { list ->
            list.map { it.toDetail() }
        }
    }

    suspend fun listTemplates(locationId: Long): List<TemplateDetail> {
        return templateDao.listTemplatesWithItems(locationId).map { it.toDetail() }
    }

    suspend fun createTemplate(locationId: Long, name: String, holidayCode: String?): Long {
        val template = TemplateEntity(
            id = generateId(),
            name = name,
            locationId = locationId,
            holidayCode = holidayCode
        )
        templateDao.upsert(template)
        return template.id
    }

    suspend fun updateTemplateInfo(templateId: Long, name: String, holidayCode: String?) {
        val existing = templateDao.getTemplateById(templateId) ?: return
        templateDao.upsert(
            existing.copy(
                name = name,
                holidayCode = holidayCode
            )
        )
    }

    suspend fun deleteTemplate(templateId: Long) {
        templateDao.deleteTemplate(templateId)
        templateItemDao.deleteForTemplate(templateId)
    }

    suspend fun updateTemplateItemQuantity(templateId: Long, itemId: Long, quantity: Int) {
        val safeQuantity = quantity.coerceAtLeast(0)
        templateItemDao.upsertAll(
            listOf(
                TemplateItemEntity(
                    templateId = templateId,
                    itemId = itemId,
                    startQuantity = safeQuantity
                )
            )
        )
    }

    suspend fun addItemToTemplate(templateId: Long, itemId: Long, defaultQuantity: Int = 0) {
        templateItemDao.upsertAll(
            listOf(
                TemplateItemEntity(
                    templateId = templateId,
                    itemId = itemId,
                    startQuantity = defaultQuantity.coerceAtLeast(0)
                )
            )
        )
    }

    suspend fun removeItemFromTemplate(templateId: Long, itemId: Long) {
        val current = templateItemDao.listForTemplate(templateId)
        val remaining = current.filter { it.itemId != itemId }
        templateItemDao.deleteForTemplate(templateId)
        if (remaining.isNotEmpty()) {
            templateItemDao.upsertAll(remaining)
        }
    }

    suspend fun listAvailableItemsForTemplate(locationId: Long, templateId: Long): List<AvailableItem> {
        val allItems = itemDao.listItemsForLocation(locationId)
        val existingItemIds = templateItemDao.listForTemplate(templateId).map { it.itemId }.toSet()
        return allItems
            .filter { it.id !in existingItemIds }
            .map { item ->
                AvailableItem(
                    id = item.id,
                    name = item.name,
                    sku = item.sku
                )
            }
    }

    suspend fun getTemplateDetail(templateId: Long): TemplateDetail? {
        return templateDao.getTemplateWithItems(templateId)?.toDetail()
    }

    private fun TemplateWithItems.toDetail(): TemplateDetail {
        val items = items.map { it.toDetail(template.id) }
        return TemplateDetail(
            id = template.id,
            name = template.name,
            holidayCode = template.holidayCode,
            locationId = template.locationId,
            items = items
        )
    }

    private fun TemplateItemWithItem.toDetail(templateId: Long): TemplateItemDetail {
        return TemplateItemDetail(
            templateId = templateId,
            itemId = templateItem.itemId,
            itemName = item.name,
            sku = item.sku,
            startQuantity = templateItem.startQuantity
        )
    }

    private fun generateId(): Long = System.currentTimeMillis()
}

data class TemplateDetail(
    val id: Long,
    val name: String,
    val holidayCode: String?,
    val locationId: Long,
    val items: List<TemplateItemDetail>
)

data class TemplateItemDetail(
    val templateId: Long,
    val itemId: Long,
    val itemName: String,
    val sku: String?,
    val startQuantity: Int
)

data class AvailableItem(
    val id: Long,
    val name: String,
    val sku: String?
)
