package com.lokin.app.data.local.dao

import androidx.room.*
import com.lokin.app.data.local.entity.ExerciseLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseLogDao {
    @Query("SELECT * FROM exercise_logs WHERE uid = :uid ORDER BY date DESC")
    fun observeAll(uid: String): Flow<List<ExerciseLogEntity>>

    @Query("""
        SELECT COALESCE(SUM(caloriesBurned), 0) FROM exercise_logs 
        WHERE uid = :uid AND date >= :dayStart AND date < :dayEnd
    """)
    fun observeDayCalories(uid: String, dayStart: Long, dayEnd: Long): Flow<Float>

    @Insert
    suspend fun insert(log: ExerciseLogEntity)

    @Delete
    suspend fun delete(log: ExerciseLogEntity)
}