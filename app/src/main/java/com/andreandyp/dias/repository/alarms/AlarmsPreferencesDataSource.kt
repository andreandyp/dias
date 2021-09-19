package com.andreandyp.dias.repository.alarms

import com.andreandyp.dias.domain.Alarm

interface AlarmsPreferencesDataSource {
    fun saveAlarmPreference(id: Int, field: Alarm.Field, value: Any)
    fun getAlarmPreferences(id: Int, isNextAlarm: Boolean): Alarm
}