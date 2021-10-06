package com.andreandyp.dias.usecases

import android.location.Location
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.mocks.*
import com.andreandyp.dias.repository.sunrise.SunriseRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import java.io.IOException

class GetTomorrowSunriseUseCaseTest {
    private val location = LocationMocks.fakeLocation
    private var sunriseRepository: SunriseRepository = mock {
        onBlocking { fetchLocalSunrise(any()) } doReturn DomainMocks.sunriseLocal
        onBlocking {
            fetchAPISunrise(
                any(),
                anyString(),
                anyString()
            )
        } doReturn DomainMocks.sunriseNetwork
        on { fetchPreferencesSunrise(Origin.NO_INTERNET) } doReturn PreferencesMocks.sunriseNoInternet
        on { fetchPreferencesSunrise(Origin.NO_LOCATION) } doReturn PreferencesMocks.sunriseNoLocation
    }

    private lateinit var getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase

    @Before
    fun setUp() {
        getTomorrowSunriseUseCase = GetTomorrowSunriseUseCase(sunriseRepository)
    }

    @Test
    fun `returns a local sunrise when no update is forced`() = runBlocking {
        val sunrise = getTomorrowSunriseUseCase(location, false)
        verify(sunriseRepository).fetchLocalSunrise(any())
        assertThat(sunrise.origin).isEqualTo(Origin.DATABASE)
    }

    @Test
    fun `returns a remote sunrise when there is no local sunrise`() = runBlocking {
        whenever(sunriseRepository.fetchLocalSunrise(any())) doReturn null
        whenever(
            sunriseRepository.fetchAPISunrise(
                any(),
                any(),
                any()
            )
        ) doReturn DomainMocks.sunriseNetwork

        val sunrise = getTomorrowSunriseUseCase(location, false)

        verify(sunriseRepository).fetchLocalSunrise(any())
        verify(sunriseRepository).fetchAPISunrise(any(), anyString(), anyString())
        verify(sunriseRepository).saveDownloadedSunrise(any())
        assertThat(sunrise.origin).isEqualTo(Origin.INTERNET)
    }

    @Test
    fun `returns a remote sunrise when update is forced`() = runBlocking {
        val sunrise = getTomorrowSunriseUseCase(location, true)

        verify(sunriseRepository).fetchAPISunrise(any(), anyString(), anyString())
        assertThat(sunrise.origin).isEqualTo(Origin.INTERNET)
    }

    @Test
    fun `saves a remote sunrise when it is downloaded`() = runBlocking {
        getTomorrowSunriseUseCase(location, true)

        verify(sunriseRepository).fetchAPISunrise(any(), anyString(), anyString())
        verify(sunriseRepository).saveDownloadedSunrise(any())
    }

    @Test
    fun `returns a local sunrise when remote source isn't available`() = runBlocking {
        sunriseRepository.apply {
            whenever(fetchAPISunrise(any(), anyString(), anyString())) doAnswer {
                throw IOException()
            }
            whenever(fetchLocalSunrise(any())) doReturn DomainMocks.sunriseLocal
        }

        val sunrise = getTomorrowSunriseUseCase(location, true)

        verify(sunriseRepository).fetchAPISunrise(any(), anyString(), anyString())
        verify(sunriseRepository).fetchLocalSunrise(any())
        assertThat(sunrise.origin).isEqualTo(Origin.DATABASE)
    }

    @Test
    fun `returns a preference sunrise when remote source isn't available and there is no local sunrise`() =
        runBlocking {
            sunriseRepository.apply {
                whenever(fetchAPISunrise(any(), any(), any())) doAnswer {
                    throw IOException()
                }
                whenever(fetchLocalSunrise(any())) doReturn null
                whenever(fetchPreferencesSunrise(Origin.NO_INTERNET)) doReturn PreferencesMocks.sunriseNoInternet
            }

            val sunrise = getTomorrowSunriseUseCase(location, true)

            verify(sunriseRepository).fetchAPISunrise(any(), anyString(), anyString())
            verify(sunriseRepository).fetchLocalSunrise(any())
            verify(sunriseRepository).fetchPreferencesSunrise(Origin.NO_INTERNET)
            assertThat(sunrise.origin).isEqualTo(Origin.NO_INTERNET)
        }

    @Test
    fun `returns a preference sunrise when there is no location and an update is forced`() =
        runBlocking {
            val sunrise = getTomorrowSunriseUseCase(null, true)

            verify(sunriseRepository).fetchPreferencesSunrise(Origin.NO_LOCATION)
            assertThat(sunrise.origin).isEqualTo(Origin.NO_LOCATION)
        }

    @Test
    fun `returns a preference sunrise when there is no location, an update isn't forced and there is no local sunrise`() =
        runBlocking {
            sunriseRepository.apply {
                whenever(fetchLocalSunrise(any())) doReturn null
                whenever(fetchPreferencesSunrise(Origin.NO_LOCATION)) doReturn PreferencesMocks.sunriseNoLocation
            }

            val sunrise = getTomorrowSunriseUseCase(null, false)

            verify(sunriseRepository).fetchPreferencesSunrise(Origin.NO_LOCATION)
            assertThat(sunrise.origin).isEqualTo(Origin.NO_LOCATION)
        }
}