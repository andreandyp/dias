package com.andreandyp.dias.usecases

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import javax.inject.Inject

open class ConfigureAlarmSettingsUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
) {
    open operator fun invoke(alarmId: Int, isNextAlarm: Boolean): Alarm {
        return alarmsRepository.getAlarmPreferences(alarmId, isNextAlarm)
    }
}