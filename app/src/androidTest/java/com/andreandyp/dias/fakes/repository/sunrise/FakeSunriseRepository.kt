package com.andreandyp.dias.fakes.repository.sunrise

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.mocks.DomainMocks
import com.andreandyp.dias.mocks.PreferencesMocks
import com.andreandyp.dias.repository.sunrise.SunriseRepository
import java.time.LocalDate
import javax.inject.Inject

class FakeSunriseRepository @Inject constructor() : SunriseRepository {
    override suspend fun fetchAPISunrise(
        tomorrowDate: LocalDate,
        latitude: String,
        longitude: String
    ): Sunrise = DomainMocks.sunriseNetwork

    override suspend fun fetchLocalSunrise(tomorrowDate: LocalDate): Sunrise =
        DomainMocks.sunriseLocal

    override fun fetchPreferencesSunrise(origin: Origin): Sunrise =
        PreferencesMocks.sunriseNoInternet

    override suspend fun saveDownloadedSunrise(sunrise: Sunrise) = Unit
}