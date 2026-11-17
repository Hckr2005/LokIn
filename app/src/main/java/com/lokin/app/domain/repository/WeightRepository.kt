package com.lokin.app.domain.repository

import com.lokin.app.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    fun observeAll(uid: String): Flow<List<WeightLogEntity>>
    fun observeSince(uid: String, from: Long): Flow<List<WeightLogEntity>>
    suspend fun getLatest(uid: String): WeightLogEntity?
    suspend fun insert(log: WeightLogEntity)
    suspend fun delete(log: WeightLogEntity)
}