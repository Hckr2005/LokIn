package com.lokin.app.data.repository

import com.lokin.app.data.local.dao.VitalLogDao
import com.lokin.app.data.local.entity.VitalLogEntity
import com.lokin.app.domain.repository.VitalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VitalRepositoryImpl @Inject constructor(
    private val dao: VitalLogDao
) : VitalRepository {
    override fun observeRecent(uid: String): Flow<List<VitalLogEntity>> = dao.observeRecent(uid)
    override suspend fun insert(log: VitalLogEntity) = dao.insert(log)
    override suspend fun delete(log: VitalLogEntity) = dao.delete(log)
}