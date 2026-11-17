package com.lokin.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,
    val date: Long,         // epoch millis — dùng System.currentTimeMillis()
    val weightKg: Float
)