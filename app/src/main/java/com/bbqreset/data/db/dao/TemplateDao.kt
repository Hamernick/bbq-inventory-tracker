package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bbqreset.data.db.entity.TemplateEntity
import com.bbqreset.data.db.model.TemplateWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates WHERE location_id = :locationId ORDER BY name")
    fun observeTemplates(locationId: Long): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE location_id = :locationId ORDER BY name")
    suspend fun listTemplates(locationId: Long): List<TemplateEntity>

    @Transaction
    @Query("SELECT * FROM templates WHERE location_id = :locationId ORDER BY name")
    fun observeTemplatesWithItems(locationId: Long): Flow<List<TemplateWithItems>>

    @Transaction
    @Query("SELECT * FROM templates WHERE location_id = :locationId ORDER BY name")
    suspend fun listTemplatesWithItems(locationId: Long): List<TemplateWithItems>

    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: Long): TemplateEntity?

    @Transaction
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateWithItems(templateId: Long): TemplateWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(template: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(templates: List<TemplateEntity>)

    @Query("DELETE FROM templates WHERE id = :templateId")
    suspend fun deleteTemplate(templateId: Long)
}
