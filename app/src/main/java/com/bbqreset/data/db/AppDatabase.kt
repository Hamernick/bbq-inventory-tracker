package com.bbqreset.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bbqreset.data.db.dao.CounterDao
import com.bbqreset.data.db.dao.ItemDao
import com.bbqreset.data.db.dao.JobDao
import com.bbqreset.data.db.dao.LocationDao
import com.bbqreset.data.db.dao.LogDao
import com.bbqreset.data.db.dao.TemplateDao
import com.bbqreset.data.db.dao.TemplateItemDao
import com.bbqreset.data.db.entity.CounterEntity
import com.bbqreset.data.db.entity.ItemEntity
import com.bbqreset.data.db.entity.JobEntity
import com.bbqreset.data.db.entity.LocationEntity
import com.bbqreset.data.db.entity.LogEntity
import com.bbqreset.data.db.entity.TemplateEntity
import com.bbqreset.data.db.entity.TemplateItemEntity

@Database(
    entities = [
        LocationEntity::class,
        ItemEntity::class,
        TemplateEntity::class,
        TemplateItemEntity::class,
        CounterEntity::class,
        LogEntity::class,
        JobEntity::class
    ],
    version = AppDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(AppDatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun itemDao(): ItemDao
    abstract fun templateDao(): TemplateDao
    abstract fun templateItemDao(): TemplateItemDao
    abstract fun counterDao(): CounterDao
    abstract fun logDao(): LogDao
    abstract fun jobDao(): JobDao

    companion object {
        const val VERSION = 1
        const val NAME = "bbq-reset.db"
    }
}
