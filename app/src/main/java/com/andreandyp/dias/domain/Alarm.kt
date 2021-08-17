package com.andreandyp.dias.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import java.time.DayOfWeek
import java.time.LocalDateTime

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
        }

    @get:Bindable
    var offsetMinutes: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.offsetMinutes)
        }

    @get:Bindable
    var offsetType: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.offsetType)
        }

    @get:Bindable
    var day: DayOfWeek? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.day)
        }

    @get:Bindable
    var ringingAt: LocalDateTime? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.ringingAt)
        }

    enum class Field {
        ON, VIBRATION, TONE, URI_TONE, OFFSET_HOURS, OFFSET_MINUTES, OFFSET_TYPE
    }
}