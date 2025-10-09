package com.bbqreset.data.db

import androidx.room.TypeConverter
import com.bbqreset.data.db.entity.JobStatus

class AppDatabaseConverters {
    @TypeConverter
    fun fromJobStatus(status: JobStatus?): String? = status?.name

    @TypeConverter
    fun toJobStatus(value: String?): JobStatus? = value?.let { enumValueOf<JobStatus>(it) }
}
