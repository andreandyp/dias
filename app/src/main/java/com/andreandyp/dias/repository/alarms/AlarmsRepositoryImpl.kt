package com.andreandyp.dias.repository.alarms

import com.andreandyp.dias.domain.Alarm
import javax.inject.Inject

class AlarmsRepositoryImpl @Inject constructor(
    private val alarmsPreferencesDataSource: AlarmsPreferencesDataSource
) : AlarmsRepository {
    override fun getAlarmPreferences(id: Int, isNextAlarm: Boolean): Alarm {
        return alarmsPreferencesDataSource.getAlarmPreferences(id, isNextAlarm)
    }

    override fun saveOnSetting(id: Int, value: Boolean) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.ON, value)
    }

    override fun saveVibrationSetting(id: Int, value: Boolean) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.VIBRATION, value)
    }

    override fun saveToneSetting(id: Int, value: String?) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.TONE, value ?: "")
    }

    override fun saveUriToneSetting(id: Int, value: String?) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.URI_TONE, value ?: "")
    }

    override fun saveOffsetHoursSetting(id: Int, value: Int) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.OFFSET_HOURS, value)
    }

    override fun saveOffsetMinutesSetting(id: Int, value: Int) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.OFFSET_MINUTES, value)
    }

    override fun saveOffsetTypeSetting(id: Int, value: Int) {
        alarmsPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.OFFSET_TYPE, value)
    }
}