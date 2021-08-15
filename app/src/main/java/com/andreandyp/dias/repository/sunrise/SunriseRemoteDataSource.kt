package com.andreandyp.dias.repository.sunrise

import com.andreandyp.dias.domain.Sunrise
import java.time.LocalDate

interface SunriseRemoteDataSource {
    suspend fun fetchSunrise(tomorrowDate: LocalDate, latitude: String, longitude: String): Sunrise
}