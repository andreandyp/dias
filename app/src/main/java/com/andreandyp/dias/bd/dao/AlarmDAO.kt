package com.andreandyp.dias.bd.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.andreandyp.dias.bd.entities.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDAO {
    @Query("SELECT * FROM Alarms")
    fun selectAll(): Flow<List<AlarmEntity>>

    @Insert
    suspend fun insertDefaultAlarms(alarms: List<AlarmEntity>)

    @Query("SELECT COUNT(*) FROM Alarms")
    suspend fun countAlarms(): Int

    @Query("SELECT * FROM Alarms WHERE id = :id")
    suspend fun getAlarmById(id: Int): AlarmEntity

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)
}
