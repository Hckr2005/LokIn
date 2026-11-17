package com.lokin.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vital_logs")
data class VitalLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uid: String,
    val date: Long,
    val heartRate: Int? = null,
    val systolic: Int? = null,      // huyết áp tâm thu (mmHg)
    val diastolic: Int? = null      // huyết áp tâm trương (mmHg)
)