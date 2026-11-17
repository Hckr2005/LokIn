package com.lokin.app.util

import com.lokin.app.data.local.entity.SleepLogEntity

object SleepUtil {
    /** Khoảng giờ ngủ khuyến nghị theo tuổi (min, max). */
    fun recommended(age: Int): Pair<Int, Int> = when {
        age <= 0 -> 7 to 9
        age < 14 -> 9 to 11
        age < 18 -> 8 to 10
        age < 65 -> 7 to 9
        else -> 7 to 8
    }

    fun hours(log: SleepLogEntity): Float =
        (log.wakeTimeMs - log.bedtimeMs) / 3_600_000f
}