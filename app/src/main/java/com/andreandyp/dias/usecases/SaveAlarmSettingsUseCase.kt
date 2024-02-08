package com.andreandyp.dias.usecases

import android.net.Uri
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
            Alarm.Field.TONE -> alarmsRepository.saveToneSetting(id, value as String)
            Alarm.Field.URI_TONE -> alarmsRepository.saveUriToneSetting(id, value as Uri)
            else -> Unit
            /*
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
