package com.andreandyp.dias.repository

import com.andreandyp.dias.domain.Sunrise
import java.time.LocalDate

interface SunriseLocalDataSource {
    suspend fun fetchSunrise(tomorrowDate: LocalDate): Sunrise?
    suspend fun saveSunrise(sunrise: Sunrise)
}