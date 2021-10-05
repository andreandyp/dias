package com.andreandyp.dias.usecases

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import javax.inject.Inject

open class SaveAlarmSettingsUseCase @Inject constructor(
    private val alarmsRepository: AlarmsRepository
) {
    open operator fun invoke(alarm: Alarm, field: Alarm.Field) {
        val (id) = alarm
        when (field) {
            Alarm.Field.ON -> alarmsRepository.saveOnSetting(id, alarm.on)
            Alarm.Field.VIBRATION -> alarmsRepository.saveVibrationSetting(id, alarm.vibration)
            Alarm.Field.TONE -> alarmsRepository.saveToneSetting(id, alarm.tone)
            Alarm.Field.URI_TONE -> alarmsRepository.saveUriToneSetting(id, alarm.uriTone)
            Alarm.Field.OFFSET_HOURS -> {
                alarmsRepository.saveOffsetHoursSetting(id, alarm.offsetHours)
            }
            Alarm.Field.OFFSET_MINUTES -> {
                alarmsRepository.saveOffsetMinutesSetting(id, alarm.offsetMinutes)
            }
            Alarm.Field.OFFSET_TYPE -> alarmsRepository.saveOffsetTypeSetting(id, alarm.offsetType)
        }
    }
}