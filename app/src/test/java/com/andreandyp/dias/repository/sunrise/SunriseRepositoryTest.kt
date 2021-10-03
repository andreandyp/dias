package com.andreandyp.dias.repository.sunrise

import com.andreandyp.dias.bd.DatabaseMocks
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.network.NetworkMocks
import com.andreandyp.dias.preferences.PreferencesMocks
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate

class SunriseRepositoryTest {
    private lateinit var sunrisePreferenceDataSource: SunrisePreferenceDataSource
    private lateinit var sunriseLocalDataSource: SunriseLocalDataSource
    private lateinit var sunriseRemoteDataSource: SunriseRemoteDataSource

    private val repository by lazy {
        SunriseRepository(
            sunrisePreferenceDataSource,
            sunriseLocalDataSource,
            sunriseRemoteDataSource
        )
    }

    @Before
    fun setUp() {
        sunrisePreferenceDataSource = mock {
            on { fetchSunrise(any()) } doReturn PreferencesMocks.sunriseNoInternet
        }

        sunriseLocalDataSource = mock {
            onBlocking { fetchSunrise(any()) } doReturn DatabaseMocks.sunrise
        }

        sunriseRemoteDataSource = mock {
            onBlocking { fetchSunrise(any(), any(), any()) } doReturn NetworkMocks.sunrise
        }
    }

    @Test
    fun `fetches sunrise from api`() = runBlocking {
        val sunrise = repository.fetchAPISunrise(LocalDate.now(), "", "")
        assertThat(sunrise.origin).isEqualTo(Origin.INTERNET)
    }

    @Test
    fun `fetches sunrise from local`() = runBlocking {
        val sunrise = repository.fetchLocalSunrise(LocalDate.now())
        assertThat(sunrise!!.origin).isEqualTo(Origin.DATABASE)
    }

    @Test
    fun `returns null when there is no sunrise in local`() = runBlocking {
        sunriseLocalDataSource = mock {
            onBlocking { fetchSunrise(any()) } doReturn null
        }

        val sunrise = repository.fetchLocalSunrise(LocalDate.now())
        assertThat(sunrise).isNull()
    }

    @Test
    fun `fetches sunrise from preferences when there is no internet`() = runBlocking {
        val sunrise = repository.fetchPreferencesSunrise(Origin.NO_INTERNET)
        assertThat(sunrise.origin).isEqualTo(Origin.NO_INTERNET)
    }

    @Test
    fun `fetches sunrise from preferences when there is no location`() = runBlocking {
        sunrisePreferenceDataSource = mock {
            on { fetchSunrise(any()) } doReturn PreferencesMocks.sunriseNoLocation
        }

        val sunrise = repository.fetchPreferencesSunrise(Origin.NO_LOCATION)
        assertThat(sunrise.origin).isEqualTo(Origin.NO_LOCATION)
    }

    @Test
    fun `saves downloaded sunrise in local`() = runBlocking {
        val downloadedSunrise = NetworkMocks.sunrise
        repository.saveDownloadedSunrise(downloadedSunrise)
        Mockito.verify(sunriseLocalDataSource).saveSunrise(downloadedSunrise)
    }
}