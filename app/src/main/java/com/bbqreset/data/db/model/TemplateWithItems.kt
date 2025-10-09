package com.bbqreset.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.bbqreset.data.db.entity.ItemEntity
import com.bbqreset.data.db.entity.TemplateEntity
import com.bbqreset.data.db.entity.TemplateItemEntity

data class TemplateWithItems(
    @Embedded
    val template: TemplateEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "template_id",
        entity = TemplateItemEntity::class
    )
    val items: List<TemplateItemWithItem>
)

data class TemplateItemWithItem(
    @Embedded
    val templateItem: TemplateItemEntity,
    @Relation(
        parentColumn = "item_id",
        entityColumn = "id"
    )
    val item: ItemEntity
)
