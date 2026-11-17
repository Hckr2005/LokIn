package com.lokin.app.data.local.dao

import androidx.room.*
import com.lokin.app.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs WHERE uid = :uid ORDER BY date DESC")
    fun observeAll(uid: String): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_logs WHERE uid = :uid AND date >= :from ORDER BY date ASC")
    fun observeSince(uid: String, from: Long): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_logs WHERE uid = :uid ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(uid: String): WeightLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WeightLogEntity)

    @Delete
    suspend fun delete(log: WeightLogEntity)
}