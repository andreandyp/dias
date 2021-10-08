package com.andreandyp.dias.fakes.usecases

import android.location.Location
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.mocks.LocationMocks
import com.andreandyp.dias.repository.sunrise.SunriseRepository
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import java.time.LocalDate
import javax.inject.Inject

class FakeGetTomorrowSunriseUseCase @Inject constructor(
    private val sunriseRepository: SunriseRepository
) : GetTomorrowSunriseUseCase(sunriseRepository) {
    var originToFetch: Origin = Origin.INTERNET
    private val tomorrowDate = LocalDate.now().plusDays(1)

    override suspend operator fun invoke(location: Location?, forceUpdate: Boolean): Sunrise {
        return when (originToFetch) {
            Origin.INTERNET -> sunriseRepository.fetchAPISunrise(
                tomorrowDate,
                LocationMocks.fakeLatitude.toString(),
                LocationMocks.fakeLongitude.toString(),
            )
            Origin.DATABASE -> sunriseRepository.fetchLocalSunrise(tomorrowDate)!!
            else -> sunriseRepository.fetchPreferencesSunrise(originToFetch)
        }
    }
}