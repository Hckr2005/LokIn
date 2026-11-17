package com.lokin.app.data.local.dao

import androidx.room.*
import com.lokin.app.data.local.entity.SleepLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepLogDao {
    @Query("SELECT * FROM sleep_logs WHERE uid = :uid ORDER BY date DESC LIMIT 30")
    fun observeRecent(uid: String): Flow<List<SleepLogEntity>>

    @Query("SELECT * FROM sleep_logs WHERE uid = :uid AND date >= :dayStart AND date < :dayEnd LIMIT 1")
    fun observeDay(uid: String, dayStart: Long, dayEnd: Long): Flow<SleepLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SleepLogEntity)

    @Delete
    suspend fun delete(log: SleepLogEntity)
}