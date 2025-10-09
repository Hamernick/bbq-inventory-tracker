package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "templates",
    indices = [
        Index(value = ["location_id"]),
        Index(value = ["holiday_code"])
    ]
)
data class TemplateEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    @ColumnInfo(name = "location_id")
    val locationId: Long,
    @ColumnInfo(name = "holiday_code")
    val holidayCode: String?
)
