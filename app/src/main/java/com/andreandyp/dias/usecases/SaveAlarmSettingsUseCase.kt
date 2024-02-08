package com.andreandyp.dias.usecases

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import javax.inject.Inject

open class SaveAlarmSettingsUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
) {
    open suspend operator fun invoke(id: Int, field: Alarm.Field, value: Any) {
        when (field) {
            Alarm.Field.ON -> alarmsRepository.saveOnSetting(id, value as Boolean)
            Alarm.Field.VIBRATION -> alarmsRepository.saveVibrationSetting(id, value as Boolean)
            else -> Unit
            /*
            Alarm.Field.TONE -> alarmsRepository.saveToneSetting(id, alarm.tone)
            Alarm.Field.URI_TONE -> alarmsRepository.saveUriToneSetting(id, alarm.uriTone)
            Alarm.Field.OFFSET_HOURS -> {
                alarmsRepository.saveOffsetHoursSetting(id, alarm.offsetHours)
            }

            Alarm.Field.OFFSET_MINUTES -> {
                alarmsRepository.saveOffsetMinutesSetting(id, alarm.offsetMinutes)
            }

            Alarm.Field.OFFSET_TYPE -> alarmsRepository.saveOffsetTypeSetting(id, alarm.offsetType)*/
        }
    }
}
