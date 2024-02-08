package com.andreandyp.dias.repository.alarms

import android.net.Uri
import com.andreandyp.dias.data.local.datasources.AlarmsLocalDataSource
import com.andreandyp.dias.domain.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmsRepositoryImpl @Inject constructor(
    private val alarmsPreferencesDataSource: AlarmsPreferencesDataSource,
    private val alarmsLocalDataSource: AlarmsLocalDataSource,
) : AlarmsRepository {
    override suspend fun getAlarms(): Flow<List<Alarm>> {
        return alarmsLocalDataSource.getAllAlarms().map { entities ->
            entities.map {
                Alarm(
                    it.id,
                    it.isNextAlarm,
                    it.on,
                    it.shouldVibrate,
                    it.ringtone,
                    it.uriTone,
                    it.offsetHours,
                    it.offsetMinutes,
                    it.offsetType,
                    it.utcRingingAt,
                )
            }
        }
    }

    override suspend fun saveOnSetting(id: Int, value: Boolean) {
        alarmsLocalDataSource.saveOnConfig(id, value)
    }

    override suspend fun saveVibrationSetting(id: Int, value: Boolean) {
        alarmsLocalDataSource.saveVibrationConfig(id, value)
    }

    override suspend fun saveToneSetting(id: Int, value: String) {
        alarmsLocalDataSource.saveRingtoneTitle(id, value)
    }

    override suspend fun saveUriToneSetting(id: Int, value: Uri) {
        alarmsLocalDataSource.saveRingtoneUri(id, value)
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
