package com.bbqreset.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbqreset.data.db.entity.TemplateItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateItemDao {
    @Query("SELECT * FROM template_items WHERE template_id = :templateId ORDER BY item_id")
    fun observeForTemplate(templateId: Long): Flow<List<TemplateItemEntity>>

    @Query("SELECT * FROM template_items WHERE template_id = :templateId ORDER BY item_id")
    suspend fun listForTemplate(templateId: Long): List<TemplateItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<TemplateItemEntity>)

    @Query("DELETE FROM template_items WHERE template_id = :templateId")
    suspend fun deleteForTemplate(templateId: Long)
}
