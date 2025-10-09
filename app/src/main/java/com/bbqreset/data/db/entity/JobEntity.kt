package com.bbqreset.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jobs",
    indices = [
        Index(value = ["kind"]),
        Index(value = ["scheduled_for"]),
        Index(value = ["dedupe_key"], unique = true)
    ]
)
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val kind: String,
    @ColumnInfo(name = "scheduled_for")
    val scheduledForEpochSeconds: Long,
    val status: JobStatus,
    @ColumnInfo(name = "last_error")
    val lastError: String?,
    @ColumnInfo(name = "dedupe_key")
    val dedupeKey: String?
)

enum class JobStatus {
    PENDING,
    RUNNING,
    DONE,
    ERROR
}
