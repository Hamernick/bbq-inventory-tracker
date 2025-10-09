package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "logs",
    indices = [
        Index(value = ["ts"]),
        Index(value = ["actor"])
    ]
)
data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ts: Long,
    val actor: String,
    val action: String,
    @ColumnInfo(name = "meta_json")
    val metaJson: String
)
