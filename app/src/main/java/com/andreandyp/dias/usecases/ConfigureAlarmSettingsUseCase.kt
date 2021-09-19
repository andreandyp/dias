package com.andreandyp.dias.usecases

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import javax.inject.Inject

class ConfigureAlarmSettingsUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
) {
    operator fun invoke(alarmId: Int): Alarm {
        return alarmsRepository.getAlarmPreferences(alarmId)
    }
}