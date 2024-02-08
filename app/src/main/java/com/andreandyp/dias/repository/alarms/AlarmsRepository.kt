package com.andreandyp.dias.repository.alarms

import android.net.Uri
import com.andreandyp.dias.domain.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmsRepository {
    suspend fun getAlarms(): Flow<List<Alarm>>
    suspend fun saveOnSetting(id: Int, value: Boolean)
    suspend fun saveVibrationSetting(id: Int, value: Boolean)
    suspend fun saveToneSetting(id: Int, value: String)
    suspend fun saveUriToneSetting(id: Int, value: Uri)
    fun saveOffsetHoursSetting(id: Int, value: Int)
    fun saveOffsetMinutesSetting(id: Int, value: Int)
    fun saveOffsetTypeSetting(id: Int, value: Int)
}
