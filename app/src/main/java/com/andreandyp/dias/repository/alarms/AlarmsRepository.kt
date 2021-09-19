package com.andreandyp.dias.repository.alarms

import com.andreandyp.dias.domain.Alarm
import javax.inject.Inject

class AlarmsRepository @Inject constructor(
    private val alarmsPreferencesDataSource: AlarmsPreferencesDataSource
) {
    fun getAlarmPreferences(id: Int): Alarm {
        return alarmsPreferencesDataSource.getAlarmPreferences(id)
    }

    fun saveOnSetting(id: Int, value: Boolean) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.ON, value)
    }

    fun saveVibrationSetting(id: Int, value: Boolean) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.VIBRATION, value)
    }

    fun saveToneSetting(id: Int, value: String?) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.TONE, value ?: "")
    }

    fun saveUriToneSetting(id: Int, value: String?) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.TONE, value ?: "")
    }

    fun saveOffsetHoursToneSetting(id: Int, value: Int) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.OFFSET_HOURS, value)
    }

    fun saveOffsetMinutesSetting(id: Int, value: Int) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.OFFSET_MINUTES, value)
    }

    fun saveOffsetTypeSetting(id: Int, value: Int) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.OFFSET_TYPE, value)
    }
}