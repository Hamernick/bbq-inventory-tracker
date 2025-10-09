package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    @ColumnInfo(name = "tz")
    val timeZoneId: String
)
