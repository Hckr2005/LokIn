package com.lokin.app.data.repository

import com.lokin.app.data.local.dao.ExerciseLogDao
import com.lokin.app.data.local.entity.ExerciseLogEntity
import com.lokin.app.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val dao: ExerciseLogDao
) : ExerciseRepository {
    override fun observeAll(uid: String): Flow<List<ExerciseLogEntity>> = dao.observeAll(uid)
    override fun observeDayCalories(uid: String, dayStart: Long, dayEnd: Long): Flow<Float> =
        dao.observeDayCalories(uid, dayStart, dayEnd)
    override suspend fun insert(log: ExerciseLogEntity) = dao.insert(log)
    override suspend fun delete(log: ExerciseLogEntity) = dao.delete(log)
}