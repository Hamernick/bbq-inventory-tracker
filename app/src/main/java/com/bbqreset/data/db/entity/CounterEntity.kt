package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "counters",
    primaryKeys = ["date", "item_id", "location_id"],
    indices = [
        Index(value = ["date", "item_id", "location_id"], unique = true),
        Index(value = ["item_id"]),
        Index(value = ["location_id"])
    ]
)
data class CounterEntity(
    val date: String,
    @ColumnInfo(name = "item_id")
    val itemId: Long,
    @ColumnInfo(name = "location_id")
    val locationId: Long,
    @ColumnInfo(name = "start_qty")
    val startQuantity: Int,
    @ColumnInfo(name = "sold_qty")
    val soldQuantity: Int,
    @ColumnInfo(name = "manual_adj")
    val manualAdjustment: Int,
    @ColumnInfo(name = "closed_on")
    val closedOnEpochSeconds: Long?
)
