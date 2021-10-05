package com.andreandyp.dias.usecases

import android.location.Location
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.repository.sunrise.SunriseRepository
import java.time.LocalDate
import javax.inject.Inject

open class GetTomorrowSunriseUseCase @Inject constructor(
    private val sunriseRepository: SunriseRepository
) {
    private val tomorrowDate = LocalDate.now().plusDays(1)

    open suspend operator fun invoke(location: Location?, forceUpdate: Boolean): Sunrise {
        if (!forceUpdate) {
            val localSunrise = sunriseRepository.fetchLocalSunrise(tomorrowDate)
            if (localSunrise != null) {
                return localSunrise
            }
        }

        return location?.let {
            try {
                val sunriseAPI = sunriseRepository.fetchAPISunrise(
                    tomorrowDate,
                    it.latitude.toString(),
                    it.longitude.toString()
                )
                sunriseRepository.saveDownloadedSunrise(sunriseAPI)
                return sunriseAPI
            } catch (e: Exception) {
                val localSunrise = sunriseRepository.fetchLocalSunrise(tomorrowDate)
                if (localSunrise != null) {
                    return localSunrise
                }

                return sunriseRepository.fetchPreferencesSunrise(Origin.NO_INTERNET)
            }
        } ?: sunriseRepository.fetchPreferencesSunrise(Origin.NO_LOCATION)
    }
}