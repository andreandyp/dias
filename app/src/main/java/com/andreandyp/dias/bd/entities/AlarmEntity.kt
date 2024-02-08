package com.andreandyp.dias.bd.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreandyp.dias.domain.OffsetType
import java.time.OffsetDateTime

@Entity(tableName = "Alarms")
data class AlarmEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "is_next_alarm")
    val isNextAlarm: Boolean = false,
    @ColumnInfo
    val on: Boolean = false,
    @ColumnInfo(name = "should_vibrate")
    val shouldVibrate: Boolean = false,
    @ColumnInfo(name = "ringtone")
    val ringtone: String? = null,
    @ColumnInfo(name = "uri_tone")
    val uriTone: String? = null,
    @ColumnInfo(name = "offset_hours")
    val offsetHours: Int = 0,
    @ColumnInfo(name = "offset_minutes")
    val offsetMinutes: Int = 0,
    @ColumnInfo(name = "offset_type")
    val offsetType: OffsetType? = null,
    @ColumnInfo(name = "utc_ringing_at")
    val utcRingingAt: OffsetDateTime? = null,
)
