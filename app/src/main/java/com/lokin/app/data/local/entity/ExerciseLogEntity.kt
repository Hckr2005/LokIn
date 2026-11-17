package com.lokin.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_logs")
data class ExerciseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,
    val date: Long,
    val exerciseType: String,       // "Running", "Cycling", "Walking"…
    val durationMin: Int,
    val met: Float,                 // Metabolic Equivalent of Task
    val caloriesBurned: Float       // = met × weightKg × (durationMin / 60)
)