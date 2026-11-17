package com.lokin.app.domain.repository

import com.lokin.app.data.local.entity.ExerciseLogEntity
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeAll(uid: String): Flow<List<ExerciseLogEntity>>
    fun observeDayCalories(uid: String, dayStart: Long, dayEnd: Long): Flow<Float>
    suspend fun insert(log: ExerciseLogEntity)
    suspend fun delete(log: ExerciseLogEntity)
}