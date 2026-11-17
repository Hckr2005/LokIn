package com.lokin.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_logs")
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,
    val date: Long,
    val mealType: String,           // "breakfast" | "lunch" | "dinner" | "snack"
    val foodName: String,
    val calories: Float,
    val proteinG: Float,
    val carbG: Float,
    val fatG: Float
)