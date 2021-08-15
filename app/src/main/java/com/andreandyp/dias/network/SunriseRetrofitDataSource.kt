package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.repository.sunrise.SunriseRemoteDataSource
import java.time.LocalDate

class SunriseRetrofitDataSource(
    private val sunriseSunsetService: SunriseSunsetService
) : SunriseRemoteDataSource {
    override suspend fun fetchSunrise(
        tomorrowDate: LocalDate,
        latitude: String,
        longitude: String
    ): Sunrise {
        return sunriseSunsetService
            .fetchSunrise(tomorrowDate.toString(), latitude, longitude)
            .asDomain()
    }
}