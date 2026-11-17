package com.lokin.app.util

enum class VitalLevel { NORMAL, WARNING, DANGER }

data class VitalStatus(val label: String, val level: VitalLevel)

object VitalUtil {
    fun heartRate(bpm: Int): VitalStatus = when {
        bpm < 60 -> VitalStatus("Thấp (dưới 60)", VitalLevel.WARNING)
        bpm <= 100 -> VitalStatus("Bình thường", VitalLevel.NORMAL)
        else -> VitalStatus("Cao (trên 100)", VitalLevel.WARNING)
    }

    fun bloodPressure(sys: Int, dia: Int): VitalStatus = when {
        sys > 180 || dia > 120 -> VitalStatus("Mức nguy hiểm, cần đi khám", VitalLevel.DANGER)
        sys >= 140 || dia >= 90 -> VitalStatus("Tăng huyết áp độ 2", VitalLevel.DANGER)
        sys >= 130 || dia >= 80 -> VitalStatus("Tăng huyết áp độ 1", VitalLevel.WARNING)
        sys >= 120 -> VitalStatus("Hơi cao", VitalLevel.WARNING)
        sys < 90 || dia < 60 -> VitalStatus("Thấp", VitalLevel.WARNING)
        else -> VitalStatus("Bình thường", VitalLevel.NORMAL)
    }
}