package com.lokin.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,
    val date: Long,
    val bedtimeMs: Long,            // giờ đi ngủ (epoch millis)
    val wakeTimeMs: Long,           // giờ thức dậy
    val quality: Int                // 1 (rất tệ) → 5 (rất tốt)
)