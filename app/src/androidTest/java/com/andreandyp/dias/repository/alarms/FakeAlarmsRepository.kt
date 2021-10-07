package com.andreandyp.dias.repository.alarms

import com.andreandyp.dias.domain.Alarm
import javax.inject.Inject

class FakeAlarmsRepository @Inject constructor() : AlarmsRepository {
    override fun getAlarmPreferences(id: Int, isNextAlarm: Boolean): Alarm = Alarm(id, isNextAlarm)

    override fun saveOnSetting(id: Int, value: Boolean) = Unit

    override fun saveVibrationSetting(id: Int, value: Boolean) = Unit

    override fun saveToneSetting(id: Int, value: String?) = Unit

    override fun saveUriToneSetting(id: Int, value: String?) = Unit

    override fun saveOffsetHoursSetting(id: Int, value: Int) = Unit

    override fun saveOffsetMinutesSetting(id: Int, value: Int) = Unit

    override fun saveOffsetTypeSetting(id: Int, value: Int) = Unit
}