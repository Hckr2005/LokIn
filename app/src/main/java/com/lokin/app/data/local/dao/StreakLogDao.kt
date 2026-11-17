package com.lokin.app.data.local.dao

import androidx.room.*
import com.lokin.app.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE uid = :uid LIMIT 1")
    fun observe(uid: String): Flow<StreakEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(streak: StreakEntity)
}