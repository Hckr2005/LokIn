package com.lokin.app.domain.repository

import com.lokin.app.data.local.entity.VitalLogEntity
import kotlinx.coroutines.flow.Flow

interface VitalRepository {
    fun observeRecent(uid: String): Flow<List<VitalLogEntity>>
    suspend fun insert(log: VitalLogEntity)
    suspend fun delete(log: VitalLogEntity)
}