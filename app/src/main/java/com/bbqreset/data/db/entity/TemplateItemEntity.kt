package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "template_items",
    primaryKeys = ["template_id", "item_id"],
    indices = [
        Index(value = ["item_id"])
    ]
)
data class TemplateItemEntity(
    @ColumnInfo(name = "template_id")
    val templateId: Long,
    @ColumnInfo(name = "item_id")
    val itemId: Long,
    @ColumnInfo(name = "start_qty")
    val startQuantity: Int
)
