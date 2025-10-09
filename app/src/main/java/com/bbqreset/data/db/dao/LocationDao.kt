package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbqreset.data.db.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY name")
    fun observeLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations ORDER BY name")
    suspend fun listLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Long): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(locations: List<LocationEntity>)

    @Query("DELETE FROM locations")
    suspend fun clear()
}
