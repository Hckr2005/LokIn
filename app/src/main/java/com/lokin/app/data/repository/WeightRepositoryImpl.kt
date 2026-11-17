package com.lokin.app.data.repository

import com.lokin.app.data.local.dao.WeightLogDao
import com.lokin.app.data.local.entity.WeightLogEntity
import com.lokin.app.domain.repository.WeightRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeightRepositoryImpl @Inject constructor(
    private val dao: WeightLogDao
) : WeightRepository {
    override fun observeAll(uid: String): Flow<List<WeightLogEntity>> = dao.observeAll(uid)
    override fun observeSince(uid: String, from: Long): Flow<List<WeightLogEntity>> = dao.observeSince(uid, from)
    override suspend fun getLatest(uid: String): WeightLogEntity? = dao.getLatest(uid)
    override suspend fun insert(log: WeightLogEntity) = dao.insert(log)
    override suspend fun delete(log: WeightLogEntity) = dao.delete(log)
}