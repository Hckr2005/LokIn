package com.lokin.app.util

import java.util.Calendar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
object DateUtil {
    fun todayStart(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    fun todayStartFlow(): Flow<Long> = flow {
        while (true) {
            emit(todayStart())
            delay((todayEnd() - System.currentTimeMillis()).coerceAtLeast(1_000L) + 500L)
        }
    }
    fun todayEnd(): Long = todayStart() + 24 * 60 * 60 * 1000L

    fun weeksAgo(n: Int): Long = System.currentTimeMillis() - n * 7 * 24 * 60 * 60 * 1000L

    fun calcBmi(weightKg: Float, heightCm: Float): Float {
        if (heightCm <= 0) return 0f
        val h = heightCm / 100f
        return weightKg / (h * h)
    }

    fun bmiCategory(bmi: Float): String = when {
        bmi < 18.5f -> "Thiếu cân"
        bmi < 25f   -> "Bình thường"
        bmi < 30f   -> "Thừa cân"
        else        -> "Béo phì"
    }
}