package com.lokin.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lokin.app.data.local.dao.*
import com.lokin.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        WeightLogEntity::class,
        WaterLogEntity::class,
        ExerciseLogEntity::class,
        SleepLogEntity::class,
        MealLogEntity::class,
        VitalLogEntity::class,
        StreakEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun sleepLogDao(): SleepLogDao
    abstract fun mealLogDao(): MealLogDao
    abstract fun vitalLogDao(): VitalLogDao
    abstract fun streakDao(): StreakDao
}