package com.lokin.app.data.repository

import com.lokin.app.data.local.dao.SleepLogDao
import com.lokin.app.data.local.entity.SleepLogEntity
import com.lokin.app.domain.repository.SleepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SleepRepositoryImpl @Inject constructor(
    private val dao: SleepLogDao
) : SleepRepository {
    override fun observeRecent(uid: String): Flow<List<SleepLogEntity>> = dao.observeRecent(uid)
    override suspend fun insert(log: SleepLogEntity) = dao.insert(log)
    override suspend fun delete(log: SleepLogEntity) = dao.delete(log)
}