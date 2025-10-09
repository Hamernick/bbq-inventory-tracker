package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbqreset.data.db.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE location_id = :locationId ORDER BY name")
    fun observeItemsForLocation(locationId: Long): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE location_id = :locationId ORDER BY name")
    suspend fun listItemsForLocation(locationId: Long): List<ItemEntity>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): ItemEntity?

    @Query("SELECT * FROM items WHERE clover_item_id = :cloverItemId LIMIT 1")
    suspend fun getItemByCloverId(cloverItemId: String): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ItemEntity>)

    @Query("DELETE FROM items WHERE location_id = :locationId")
    suspend fun deleteForLocation(locationId: Long)
}
