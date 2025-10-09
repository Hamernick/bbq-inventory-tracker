package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbqreset.data.db.entity.LogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM logs ORDER BY ts DESC")
    fun observeLogs(): Flow<List<LogEntity>>

    @Query(
        """
        SELECT * FROM logs 
        WHERE (:actor IS NULL OR actor = :actor)
        ORDER BY ts DESC 
        LIMIT :limit
        OFFSET :offset
        """
    )
    suspend fun pageLogs(
        limit: Int,
        offset: Int,
        actor: String? = null
    ): List<LogEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: LogEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(logs: List<LogEntity>): List<Long>

    @Query("DELETE FROM logs")
    suspend fun clear()
}
