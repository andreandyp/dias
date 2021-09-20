package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Sunrise
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.IOException
import java.time.LocalDate

class SunriseRetrofitDataSourceTest {
    private lateinit var sunriseSunsetService: SunriseSunsetService
    private val sunriseNetwork = NetworkMocks.sunriseNetwork

    private val sunriseRetrofitDataSource by lazy {
        SunriseRetrofitDataSource(sunriseSunsetService)
    }

    @Before
    fun setup() {
        sunriseSunsetService = mock {
            onBlocking {
                fetchSunrise(any(), any(), any())
            } doReturn sunriseNetwork
        }
    }

    @Test
    fun `fetches data from service successfully`() = runBlocking {
        val localDate = LocalDate.now().plusDays(1)
        val result = sunriseRetrofitDataSource.fetchSunrise(localDate, "", "")
        Mockito.verify(sunriseSunsetService).fetchSunrise(localDate.toString(), "", "")
        assertThat(result, isA(Sunrise::class.java))
    }

    @Test
    fun `throws an exception when fails to fetch`(): Unit = runBlocking {
        sunriseSunsetService = mock {
            onBlocking { fetchSunrise(any(), any(), any()) } doAnswer {
                throw IOException()
            }
        }

        val localDate = LocalDate.now().plusDays(1)
        val exception = try {
            sunriseRetrofitDataSource.fetchSunrise(localDate, "", "")
            null
        } catch (e: Exception) {
            e
        }

        assertThat(exception, instanceOf(IOException::class.java))
        Mockito.verify(sunriseSunsetService).fetchSunrise(localDate.toString(), "", "")
    }
}