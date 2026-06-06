package com.rin.android.data.repository

import com.rin.android.data.local.dao.DraftDao
import com.rin.android.data.local.entity.DraftEntity
import com.rin.android.domain.repository.DraftRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftRepositoryImpl @Inject constructor(
    private val draftDao: DraftDao,
) : DraftRepository {
    override fun getAllDrafts(): Flow<List<DraftEntity>> = draftDao.getAllDrafts()
    override suspend fun getDraftById(id: Long): DraftEntity? = draftDao.getDraftById(id)
    override suspend fun saveDraft(draft: DraftEntity): Long = draftDao.insert(draft)
    override suspend fun deleteDraft(id: Long) = draftDao.deleteById(id)
}
