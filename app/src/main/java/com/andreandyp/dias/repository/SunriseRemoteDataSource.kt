package com.andreandyp.dias.repository

import com.andreandyp.dias.domain.Sunrise
import java.time.LocalDate

interface SunriseRemoteDataSource {
    suspend fun fetchSunrise(tomorrowDate: LocalDate, latitude: String, longitude: String): Sunrise
}