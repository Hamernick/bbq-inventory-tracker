package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbqreset.data.db.entity.CounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query(
        """
        SELECT * FROM counters 
        WHERE location_id = :locationId 
        AND date = :date 
        ORDER BY item_id
        """
    )
    fun observeCountersForDate(locationId: Long, date: String): Flow<List<CounterEntity>>

    @Query(
        """
        SELECT * FROM counters 
        WHERE location_id = :locationId 
        AND date = :date 
        ORDER BY item_id
        """
    )
    suspend fun listCountersForDate(locationId: Long, date: String): List<CounterEntity>

    @Query(
        """
        SELECT * FROM counters 
        WHERE date = :date 
        AND item_id = :itemId 
        AND location_id = :locationId 
        LIMIT 1
        """
    )
    suspend fun getCounter(date: String, itemId: Long, locationId: Long): CounterEntity?

    @Query(
        """
        SELECT date FROM counters 
        WHERE location_id = :locationId 
        ORDER BY date DESC 
        LIMIT 1
        """
    )
    suspend fun getMostRecentDate(locationId: Long): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(counter: CounterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(counters: List<CounterEntity>)

    @Query(
        """
        DELETE FROM counters 
        WHERE location_id = :locationId 
        AND date = :date
        """
    )
    suspend fun deleteForDate(locationId: Long, date: String)
}
