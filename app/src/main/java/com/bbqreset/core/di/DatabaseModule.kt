package com.bbqreset.core.di

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.room.Room
import com.bbqreset.data.db.AppDatabase
import com.bbqreset.data.db.AppDatabaseMigrations
import com.bbqreset.data.db.seedDebug
import java.time.Clock

object DatabaseModule {
    fun provideAppDatabase(
        context: Context,
        clock: Clock = Clock.systemUTC(),
        inMemory: Boolean = false,
        seedDebugData: Boolean = true
    ): AppDatabase {
        val builder = if (inMemory) {
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        } else {
            Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
        }

        builder.addMigrations(*AppDatabaseMigrations)

        val database = builder.build()

        val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (seedDebugData && isDebuggable) {
            database.seedDebug(clock)
        }

        return database
    }
}
