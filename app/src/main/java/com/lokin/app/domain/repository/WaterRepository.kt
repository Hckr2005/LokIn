package com.lokin.app.domain.repository

import com.lokin.app.data.local.entity.WaterLogEntity
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    fun observeDayTotal(uid: String, dayStart: Long, dayEnd: Long): Flow<Int>
    fun observeSince(uid: String, from: Long): Flow<List<WaterLogEntity>>
    suspend fun insert(log: WaterLogEntity)
    suspend fun delete(log: WaterLogEntity)
}