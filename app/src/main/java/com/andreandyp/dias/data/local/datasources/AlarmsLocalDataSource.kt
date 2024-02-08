package com.andreandyp.dias.data.local.datasources

import android.net.Uri
import com.andreandyp.dias.bd.entities.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface AlarmsLocalDataSource {
    suspend fun getAllAlarms(): Flow<List<AlarmEntity>>
    suspend fun saveOnConfig(alarmId: Int, on: Boolean)
    suspend fun saveVibrationConfig(alarmId: Int, shouldVibrate: Boolean)
    suspend fun saveRingtoneTitle(alarmId: Int, title: String)
    suspend fun saveRingtoneUri(alarmId: Int, uri: Uri)
}
