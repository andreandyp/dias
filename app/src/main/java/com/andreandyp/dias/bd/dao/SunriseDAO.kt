package com.andreandyp.dias.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.andreandyp.dias.bd.entities.SunriseEntity

@Dao
interface SunriseDAO {
    @Query("SELECT * FROM Sunrise WHERE sunriseDate = :date")
    suspend fun fetchSunrise(date: java.time.LocalDate): SunriseEntity?

    @Insert
    suspend fun saveSunrise(sunriseEntity: SunriseEntity)

    @Query("SELECT COUNT(*) FROM Sunrise")
    suspend fun countSavedSunrises(): Int

    @Query("SELECT * FROM Sunrise ORDER BY sunriseDate ASC LIMIT 1")
    suspend fun fetchOlderSunrise(): SunriseEntity

    @Delete
    suspend fun deleteSunrise(olderSunrise: SunriseEntity)
}