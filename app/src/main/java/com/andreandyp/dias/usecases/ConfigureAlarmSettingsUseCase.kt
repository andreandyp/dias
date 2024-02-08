package com.andreandyp.dias.usecases

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class ConfigureAlarmSettingsUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
) {
    open suspend operator fun invoke(): Flow<List<Alarm>> {
        return alarmsRepository.getAlarms()
        /*val nextDay = LocalDate.now().plusDays(1)[ChronoField.DAY_OF_WEEK]
        return (1..7).map {
            getAlarmPreferences(it, nextDay == it)
        }*/
    }

    /*private fun getAlarmPreferences(alarmId: Int, isNextAlarm: Boolean): Alarm {
        return alarmsRepository.getAlarmPreferences(alarmId, isNextAlarm)
    }*/
}
