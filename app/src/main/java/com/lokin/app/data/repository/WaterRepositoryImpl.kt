package com.lokin.app.data.repository

import com.lokin.app.data.local.dao.WaterLogDao
import com.lokin.app.data.local.entity.WaterLogEntity
import com.lokin.app.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WaterRepositoryImpl @Inject constructor(
    private val dao: WaterLogDao
) : WaterRepository {
    override fun observeDayTotal(uid: String, dayStart: Long, dayEnd: Long): Flow<Int> =
        dao.observeDayTotal(uid, dayStart, dayEnd)
    override fun observeSince(uid: String, from: Long): Flow<List<WaterLogEntity>> =
        dao.observeSince(uid, from)
    override suspend fun insert(log: WaterLogEntity) = dao.insert(log)
    override suspend fun delete(log: WaterLogEntity) = dao.delete(log)
}