package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbqreset.data.db.entity.WeekPlanEntity

@Dao
interface WeekPlanDao {
    @Query(
        """
        SELECT i.id AS itemId, i.name AS name, i.sku AS sku,
               i.unit_type AS unitType,
               COALESCE(w.quantity_default, 0) AS quantityDefault,
               COALESCE(w.quantity_mon, 0) AS quantityMon,
               COALESCE(w.quantity_tue, 0) AS quantityTue,
               COALESCE(w.quantity_wed, 0) AS quantityWed,
               COALESCE(w.quantity_thu, 0) AS quantityThu,
               COALESCE(w.quantity_fri, 0) AS quantityFri,
               COALESCE(w.quantity_sat, 0) AS quantitySat,
               COALESCE(w.quantity_sun, 0) AS quantitySun
        FROM items i
        LEFT JOIN week_plans w 
          ON w.item_id = i.id 
         AND w.location_id = i.location_id 
         AND w.week_start = :weekStart
        WHERE i.location_id = :locationId
        ORDER BY i.name
        """
    )
    suspend fun listPlanRows(locationId: Long, weekStart: String): List<WeekPlanRow>

    @Query("DELETE FROM week_plans WHERE item_id IN (:itemIds) AND location_id = :locationId")
    suspend fun deleteForItems(itemIds: List<Long>, locationId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WeekPlanEntity)

    @Query(
        """
        UPDATE week_plans SET quantity_default = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateDefaultQuantity(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query(
        """
        UPDATE week_plans SET quantity_mon = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateMon(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query(
        """
        UPDATE week_plans SET quantity_tue = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateTue(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query(
        """
        UPDATE week_plans SET quantity_wed = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateWed(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query(
        """
        UPDATE week_plans SET quantity_thu = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateThu(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query(
        """
        UPDATE week_plans SET quantity_fri = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateFri(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query(
        """
        UPDATE week_plans SET quantity_sat = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateSat(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query(
        """
        UPDATE week_plans SET quantity_sun = :quantity
        WHERE week_start = :weekStart AND item_id = :itemId AND location_id = :locationId
        """
    )
    suspend fun updateSun(weekStart: String, itemId: Long, locationId: Long, quantity: Int)

    @Query("DELETE FROM week_plans WHERE week_start = :weekStart AND location_id = :locationId")
    suspend fun clearWeek(weekStart: String, locationId: Long)
}

data class WeekPlanRow(
    val itemId: Long,
    val name: String,
    val sku: String?,
    val unitType: String,
    val quantityDefault: Int,
    val quantityMon: Int,
    val quantityTue: Int,
    val quantityWed: Int,
    val quantityThu: Int,
    val quantityFri: Int,
    val quantitySat: Int,
    val quantitySun: Int
)
