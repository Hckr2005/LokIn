package com.lokin.app.data.local.dao

import androidx.room.*
import com.lokin.app.data.local.entity.VitalLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalLogDao {
    @Query("SELECT * FROM vital_logs WHERE uid = :uid ORDER BY date DESC LIMIT 30")
    fun observeRecent(uid: String): Flow<List<VitalLogEntity>>

    @Insert
    suspend fun insert(log: VitalLogEntity)

    @Delete
    suspend fun delete(log: VitalLogEntity)
}