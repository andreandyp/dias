package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.mocks.LocationMocks
import com.andreandyp.dias.mocks.NetworkMocks
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.io.IOException
import java.time.LocalDate

class SunriseRetrofitDataSourceTest {
    private val fakeLatitude = LocationMocks.fakeLatitude.toString()
    private val fakeLongitude = LocationMocks.fakeLongitude.toString()
    private val sunriseNetwork = NetworkMocks.sunriseNetwork
    private var sunriseSunsetService: SunriseSunsetService = mock {
        onBlocking {
            fetchSunrise(any(), any(), any())
        } doReturn sunriseNetwork
    }

    private lateinit var sunriseRetrofitDataSource: SunriseRetrofitDataSource

    @Before
    fun setup() {
        sunriseRetrofitDataSource = SunriseRetrofitDataSource(sunriseSunsetService)
    }

    @Test
    fun `fetches data from service successfully`() = runBlocking {
        val localDate = LocalDate.now().plusDays(1)
        val result = sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)
        verify(sunriseSunsetService).fetchSunrise(localDate.toString(), fakeLatitude, fakeLongitude)
        assertThat(result).isInstanceOf(Sunrise::class.java)
    }

    @Test
    fun `throws an exception when fails to fetch`(): Unit = runBlocking {
        sunriseSunsetService.apply {
            whenever(fetchSunrise(any(), any(), any())) doAnswer {
                throw IOException()
            }
        }

        val localDate = LocalDate.now().plusDays(1)
        val exception = try {
            sunriseRetrofitDataSource.fetchSunrise(localDate, fakeLatitude, fakeLongitude)
            null
        } catch (e: Exception) {
            e
        }

        assertThat(exception).isInstanceOf(IOException::class.java)
        verify(sunriseSunsetService).fetchSunrise(localDate.toString(), fakeLatitude, fakeLongitude)
    }
}