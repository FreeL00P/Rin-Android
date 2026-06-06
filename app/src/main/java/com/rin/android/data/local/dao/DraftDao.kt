package com.rin.android.data.local.dao

import androidx.room.*
import com.rin.android.data.local.entity.DraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {
    @Query("SELECT * FROM drafts ORDER BY updatedAt DESC")
    fun getAllDrafts(): Flow<List<DraftEntity>>

    @Query("SELECT * FROM drafts WHERE id = :id")
    suspend fun getDraftById(id: Long): DraftEntity?

    @Insert
    suspend fun insert(draft: DraftEntity): Long

    @Update
    suspend fun update(draft: DraftEntity)

    @Delete
    suspend fun delete(draft: DraftEntity)

    @Query("DELETE FROM drafts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
