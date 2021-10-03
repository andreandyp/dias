package com.andreandyp.dias.repository.sunrise

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.LocalDate

interface SunriseRepository {
    suspend fun fetchAPISunrise(
        tomorrowDate: LocalDate,
        latitude: String,
        longitude: String
    ): Sunrise

    suspend fun fetchLocalSunrise(
        tomorrowDate: LocalDate
    ): Sunrise?

    fun fetchPreferencesSunrise(origin: Origin): Sunrise

    suspend fun saveDownloadedSunrise(sunrise: Sunrise)
}