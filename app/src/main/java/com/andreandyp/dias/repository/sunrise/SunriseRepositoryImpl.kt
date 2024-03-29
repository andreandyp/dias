package com.andreandyp.dias.repository.sunrise

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class SunriseRepositoryImpl @Inject constructor(
    private val sunrisePreferenceDataSource: SunrisePreferenceDataSource,
    private val sunriseLocalDataSource: SunriseLocalDataSource,
    private val sunriseRemoteDataSource: SunriseRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SunriseRepository {
    override suspend fun fetchAPISunrise(
        tomorrowDate: LocalDate,
        latitude: String,
        longitude: String
    ) = withContext(dispatcher) {
        sunriseRemoteDataSource.fetchSunrise(tomorrowDate, latitude, longitude)
    }

    override suspend fun fetchLocalSunrise(
        tomorrowDate: LocalDate
    ) = withContext(dispatcher) {
        sunriseLocalDataSource.fetchSunrise(tomorrowDate)
    }

    override fun fetchPreferencesSunrise(origin: Origin): Sunrise {
        return sunrisePreferenceDataSource.fetchSunrise(origin)
    }

    override suspend fun saveDownloadedSunrise(sunrise: Sunrise) = withContext(dispatcher) {
        sunriseLocalDataSource.saveSunrise(sunrise)
    }
}