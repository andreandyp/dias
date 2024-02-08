package com.andreandyp.dias.data.local.datasources

import com.andreandyp.dias.bd.dao.AlarmDAO
import com.andreandyp.dias.bd.entities.AlarmEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmsRoomDataSource @Inject constructor(
    private val alarmDAO: AlarmDAO,
) : AlarmsLocalDataSource {
    override suspend fun getAllAlarms(): Flow<List<AlarmEntity>> {
        if (alarmDAO.countAlarms() == 0) {
            val defaultAlarms = (1..7).map {
                AlarmEntity(it)
            }
            alarmDAO.insertDefaultAlarms(defaultAlarms)
        }

        return alarmDAO.selectAll()
    }

    override suspend fun saveOnConfig(alarmId: Int, on: Boolean) {
        val alarm = alarmDAO.getAlarmById(alarmId)
        val updatedAlarm = alarm.copy(on = on)
        alarmDAO.updateAlarm(updatedAlarm)
    }

    override suspend fun saveVibrationConfig(alarmId: Int, shouldVibrate: Boolean) {
        val alarm = alarmDAO.getAlarmById(alarmId)
        val updatedAlarm = alarm.copy(shouldVibrate = shouldVibrate)
        alarmDAO.updateAlarm(updatedAlarm)
    }
}
