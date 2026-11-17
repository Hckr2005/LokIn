package com.lokin.app.data.local.dao

import androidx.room.*
import com.lokin.app.data.local.entity.WaterLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {
    @Query("""
        SELECT COALESCE(SUM(amountMl), 0) FROM water_logs 
        WHERE uid = :uid AND date >= :dayStart AND date < :dayEnd
    """)
    fun observeDayTotal(uid: String, dayStart: Long, dayEnd: Long): Flow<Int>

    @Query("SELECT * FROM water_logs WHERE uid = :uid AND date >= :from ORDER BY date DESC")
    fun observeSince(uid: String, from: Long): Flow<List<WaterLogEntity>>

    @Insert
    suspend fun insert(log: WaterLogEntity)

    @Delete
    suspend fun delete(log: WaterLogEntity)
}