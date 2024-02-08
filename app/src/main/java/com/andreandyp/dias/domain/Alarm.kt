package com.andreandyp.dias.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.time.OffsetDateTime

data class Alarm(
    val id: Int,
    val isNextAlarm: Boolean,
    val on: Boolean,
    val vibration: Boolean,
    val ringtone: String?,
    val uriTone: String?,
    val offsetHours: Int,
    val offsetMinutes: Int,
    val offsetType: OffsetType?,
    val utcRingingAt: OffsetDateTime?,
) : BaseObservable() {

    @get:Bindable
    val formattedDate: String?
        get() {
            return null
            /*val localDateTime = ringingAt?.withZoneSameInstant(ZoneId.systemDefault())
            return localDateTime?.toLocalTime()?.toString()*/
        }

    @get:Bindable
    val formattedOffset: String
        get() {
            val offsetSymbol = when (offsetType?.type) {
                0 -> "-"
                1 -> "+"
                else -> "±"
            }
            val minutes = if (offsetMinutes == 0) "00" else offsetMinutes.toString()
            return "$offsetSymbol$offsetHours:$minutes"
        }

    enum class Field {
        ON, VIBRATION, TONE, URI_TONE, OFFSET_HOURS, OFFSET_MINUTES, OFFSET_TYPE
    }
}
