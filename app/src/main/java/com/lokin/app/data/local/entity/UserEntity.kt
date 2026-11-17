package com.lokin.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",        // "male" | "female" | "other"
    val heightCm: Float = 0f,
    val weightKg: Float = 0f,
    val goalWeightKg: Float = 0f,
    val dailyWaterGoalMl: Int = 2000,
    val dailyCalorieGoal: Int = 2000,
    val dailyStepGoal: Int = 10000,
    val dailyCalorieBurnGoal: Int = 500
)