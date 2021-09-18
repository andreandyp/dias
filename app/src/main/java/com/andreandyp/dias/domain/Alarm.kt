package com.andreandyp.dias.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*

data class Alarm(
    val id: Int
) : BaseObservable() {
    var isNextAlarm: Boolean = false

    @get:Bindable
    var on: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.on)
        }

    @get:Bindable
    var vibration: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.vibration)
        }

    @get:Bindable
    var tone: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.tone)
        }

    @get:Bindable
    var uriTone: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.uriTone)
        }

    @get:Bindable
    var offsetHours: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.offsetHours)
            notifyPropertyChanged(BR.ringingAt)
            notifyPropertyChanged(BR.formattedDate)
            notifyPropertyChanged(BR.formattedOffset)
        }

    @get:Bindable
    var offsetMinutes: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.offsetMinutes)
            notifyPropertyChanged(BR.ringingAt)
            notifyPropertyChanged(BR.formattedDate)
            notifyPropertyChanged(BR.formattedOffset)
        }

    @get:Bindable
    var offsetType: Int = -1
        set(value) {
            field = value
            notifyPropertyChanged(BR.offsetType)
            notifyPropertyChanged(BR.ringingAt)
            notifyPropertyChanged(BR.formattedDate)
            notifyPropertyChanged(BR.formattedOffset)
        }

    @get:Bindable
    var day: DayOfWeek? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.day)
            notifyPropertyChanged(BR.formattedDay)
        }

    @get:Bindable
    var utcRingingAt: ZonedDateTime? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.utcRingingAt)
        }

    @get:Bindable
    val ringingAt: ZonedDateTime?
        get() {
            return if (offsetType == 0) {
                utcRingingAt?.minusHours(offsetHours.toLong())?.minusMinutes(offsetMinutes.toLong())
            } else {
                utcRingingAt?.plusHours(offsetHours.toLong())?.plusMinutes(offsetMinutes.toLong())
            }
        }

    @get:Bindable
    val formattedDate: String?
        get() {
            val localDateTime = ringingAt?.withZoneSameInstant(ZoneId.systemDefault())
            return localDateTime?.toLocalTime()?.toString()
        }

    @get:Bindable
    val formattedOffset: String
        get() {
            val offsetSymbol = when (offsetType) {
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

    @get:Bindable
    val formattedDay: String
        get() {
            return day?.getDisplayName(TextStyle.FULL, Locale.getDefault())?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            } ?: ""
        }
}