package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "week_plans",
    primaryKeys = ["week_start", "item_id", "location_id"],
    indices = [
        Index(value = ["week_start", "location_id"]),
        Index(value = ["item_id"]),
        Index(value = ["location_id"])
    ]
)
data class WeekPlanEntity(
    @ColumnInfo(name = "week_start") val weekStart: String,
    @ColumnInfo(name = "item_id") val itemId: Long,
    @ColumnInfo(name = "location_id") val locationId: Long,
    @ColumnInfo(name = "quantity_default") val quantityDefault: Int = 0,
    @ColumnInfo(name = "quantity_mon") val quantityMon: Int = 0,
    @ColumnInfo(name = "quantity_tue") val quantityTue: Int = 0,
    @ColumnInfo(name = "quantity_wed") val quantityWed: Int = 0,
    @ColumnInfo(name = "quantity_thu") val quantityThu: Int = 0,
    @ColumnInfo(name = "quantity_fri") val quantityFri: Int = 0,
    @ColumnInfo(name = "quantity_sat") val quantitySat: Int = 0,
    @ColumnInfo(name = "quantity_sun") val quantitySun: Int = 0
)
