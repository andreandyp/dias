package com.andreandyp.dias.repository.alarms

import com.andreandyp.dias.domain.Alarm

interface AlarmsRepository {
    fun getAlarmPreferences(id: Int, isNextAlarm: Boolean): Alarm
    fun saveOnSetting(id: Int, value: Boolean)
    fun saveVibrationSetting(id: Int, value: Boolean)
    fun saveToneSetting(id: Int, value: String?)
    fun saveUriToneSetting(id: Int, value: String?)
    fun saveOffsetHoursSetting(id: Int, value: Int)
    fun saveOffsetMinutesSetting(id: Int, value: Int)
    fun saveOffsetTypeSetting(id: Int, value: Int)
}