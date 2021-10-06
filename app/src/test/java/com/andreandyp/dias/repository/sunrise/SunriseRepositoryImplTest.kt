package com.andreandyp.dias.repository.sunrise

import com.andreandyp.dias.mocks.DatabaseMocks
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.mocks.LocationMocks
import com.andreandyp.dias.mocks.NetworkMocks
import com.andreandyp.dias.mocks.PreferencesMocks
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import java.time.LocalDate

class SunriseRepositoryImplTest {
    private val sunrisePreferenceDataSource: SunrisePreferenceDataSource = mock {
        on { fetchSunrise(any()) } doReturn PreferencesMocks.sunriseNoInternet
    }
    private val sunriseLocalDataSource: SunriseLocalDataSource = mock {
        onBlocking { fetchSunrise(any()) } doReturn DatabaseMocks.sunriseFromEntity
    }
    private val sunriseRemoteDataSource: SunriseRemoteDataSource = mock {
        onBlocking {
            fetchSunrise(
                any(),
                anyString(),
                anyString()
            )
        } doReturn NetworkMocks.sunriseFromNetwork
    }

    private val tomorrowDate = LocalDate.now().plusDays(1)
    private val fakeLatitude = LocationMocks.fakeLatitude.toString()
    private val fakeLongitude = LocationMocks.fakeLongitude.toString()

    private lateinit var repository: SunriseRepositoryImpl

    @Before
    fun setUp() {
        repository = SunriseRepositoryImpl(
            sunrisePreferenceDataSource,
            sunriseLocalDataSource,
            sunriseRemoteDataSource
        )
    }

    @Test
    fun `fetches sunrise from api`() = runBlocking {
        val sunrise = repository.fetchAPISunrise(tomorrowDate, fakeLatitude, fakeLongitude)
        verify(sunriseRemoteDataSource).fetchSunrise(tomorrowDate, fakeLatitude, fakeLongitude)
        assertThat(sunrise.origin).isEqualTo(Origin.INTERNET)
    }

    @Test
    fun `fetches sunrise from local`() = runBlocking {
        val sunrise = repository.fetchLocalSunrise(tomorrowDate)
        verify(sunriseLocalDataSource).fetchSunrise(tomorrowDate)
        assertThat(sunrise!!.origin).isEqualTo(Origin.DATABASE)
    }

    @Test
    fun `returns null when there is no sunrise in local`() = runBlocking {
        sunriseLocalDataSource.apply {
            whenever(fetchSunrise(any())) doReturn null
        }

        val sunrise = repository.fetchLocalSunrise(tomorrowDate)

        verify(sunriseLocalDataSource).fetchSunrise(tomorrowDate)
        assertThat(sunrise).isNull()
    }

    @Test
    fun `fetches sunrise from preferences when there is no internet`() = runBlocking {
        val sunrise = repository.fetchPreferencesSunrise(Origin.NO_INTERNET)
        verify(sunrisePreferenceDataSource).fetchSunrise(Origin.NO_INTERNET)
        assertThat(sunrise.origin).isEqualTo(Origin.NO_INTERNET)
    }

    @Test
    fun `fetches sunrise from preferences when there is no location`() = runBlocking {
        sunrisePreferenceDataSource.apply {
            whenever(fetchSunrise(any())) doReturn PreferencesMocks.sunriseNoLocation
        }

        val sunrise = repository.fetchPreferencesSunrise(Origin.NO_LOCATION)

        verify(sunrisePreferenceDataSource).fetchSunrise(Origin.NO_LOCATION)
        assertThat(sunrise.origin).isEqualTo(Origin.NO_LOCATION)
    }

    @Test
    fun `saves downloaded sunrise in local`() = runBlocking {
        val downloadedSunrise = NetworkMocks.sunriseFromNetwork
        repository.saveDownloadedSunrise(downloadedSunrise)
        verify(sunriseLocalDataSource).saveSunrise(downloadedSunrise)
    }
}