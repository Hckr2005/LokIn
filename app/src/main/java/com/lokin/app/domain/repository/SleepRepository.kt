package com.lokin.app.domain.repository

import com.lokin.app.data.local.entity.SleepLogEntity
import kotlinx.coroutines.flow.Flow

interface SleepRepository {
    fun observeRecent(uid: String): Flow<List<SleepLogEntity>>
    suspend fun insert(log: SleepLogEntity)
    suspend fun delete(log: SleepLogEntity)
}