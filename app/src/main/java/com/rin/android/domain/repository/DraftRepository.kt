package com.rin.android.domain.repository

import com.rin.android.data.local.entity.DraftEntity
import kotlinx.coroutines.flow.Flow

interface DraftRepository {
    fun getAllDrafts(): Flow<List<DraftEntity>>
    suspend fun getDraftById(id: Long): DraftEntity?
    suspend fun saveDraft(draft: DraftEntity): Long
    suspend fun deleteDraft(id: Long)
}
