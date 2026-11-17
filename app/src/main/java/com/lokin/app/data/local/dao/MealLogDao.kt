package com.lokin.app.data.local.dao

import androidx.room.*
import com.lokin.app.data.local.entity.MealLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Query("SELECT * FROM meal_logs WHERE uid = :uid AND date >= :dayStart AND date < :dayEnd ORDER BY date ASC")
    fun observeDay(uid: String, dayStart: Long, dayEnd: Long): Flow<List<MealLogEntity>>

    @Query("""
        SELECT COALESCE(SUM(calories), 0) FROM meal_logs 
        WHERE uid = :uid AND date >= :dayStart AND date < :dayEnd
    """)
    fun observeDayCalories(uid: String, dayStart: Long, dayEnd: Long): Flow<Float>

    @Insert
    suspend fun insert(log: MealLogEntity)

    @Delete
    suspend fun delete(log: MealLogEntity)
}