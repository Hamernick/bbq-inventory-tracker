package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items",
    indices = [
        Index(value = ["clover_item_id"], unique = true),
        Index(value = ["location_id"])
    ]
)
data class ItemEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "clover_item_id")
    val cloverItemId: String?,
    val name: String,
    val sku: String?,
    @ColumnInfo(name = "location_id")
    val locationId: Long
)
