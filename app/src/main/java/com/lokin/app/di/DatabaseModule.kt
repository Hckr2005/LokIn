package com.lokin.app.di

import android.content.Context
import androidx.room.Room
import com.lokin.app.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lokin_database"
        ).build()

    @Provides fun provideUserDao(db: AppDatabase) = db.userDao()
    @Provides fun provideWeightLogDao(db: AppDatabase) = db.weightLogDao()
    @Provides fun provideWaterLogDao(db: AppDatabase) = db.waterLogDao()
    @Provides fun provideExerciseLogDao(db: AppDatabase) = db.exerciseLogDao()
    @Provides fun provideSleepLogDao(db: AppDatabase) = db.sleepLogDao()
    @Provides fun provideMealLogDao(db: AppDatabase) = db.mealLogDao()
    @Provides fun provideVitalLogDao(db: AppDatabase) = db.vitalLogDao()
    @Provides fun provideStreakDao(db: AppDatabase) = db.streakDao()
}