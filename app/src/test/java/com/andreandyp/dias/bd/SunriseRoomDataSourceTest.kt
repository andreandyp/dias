package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.dao.SunriseDAO
import com.andreandyp.dias.domain.Sunrise
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.IOException
import java.time.LocalDate

class SunriseRoomDataSourceTest {
    private lateinit var sunriseDao: SunriseDAO
    private val sunriseEntity = DatabaseMocks.sunriseEntity
    private val sunrise = DatabaseMocks.sunrise

    private val sunriseRoomDataSource by lazy {
        SunriseRoomDataSource(sunriseDao)
    }

    @Before
    fun setup() {
        sunriseDao = mock {
            onBlocking {
                fetchSunrise(any())
            } doReturn sunriseEntity
        }
    }

    @Test
    fun `fetches data from database successfully`() = runBlocking {
        val localDate = LocalDate.now().plusDays(1)
        val result = sunriseRoomDataSource.fetchSunrise(localDate)
        Mockito.verify(sunriseDao).fetchSunrise(localDate)
        MatcherAssert.assertThat(result, CoreMatchers.isA(Sunrise::class.java))
    }

    @Test
    fun `throws an exception when fails to fetch`() = runBlocking {
        sunriseDao = mock {
            onBlocking { fetchSunrise(any()) } doAnswer { throw IOException() }
        }
        val localDate = LocalDate.now().plusDays(1)
        val exception = try {
            sunriseRoomDataSource.fetchSunrise(localDate)
            null
        } catch (e: Exception) {
            e
        }

        Mockito.verify(sunriseDao).fetchSunrise(localDate)
        MatcherAssert.assertThat(exception, CoreMatchers.instanceOf(IOException::class.java))
    }

    @Test
    fun `saves a sunrise successfully`() = runBlocking {
        Mockito.`when`(sunriseDao.countSavedSunrises()).thenReturn(0)
        Mockito.`when`(sunriseDao.fetchSunrise(any())).thenReturn(null)

        sunriseRoomDataSource.saveSunrise(sunrise)
        Mockito.verify(sunriseDao).saveSunrise(sunriseEntity)
    }

    @Test
    fun `removes a sunrise when sunrises more 30`() = runBlocking {
        Mockito.`when`(sunriseDao.countSavedSunrises()).thenReturn(30)
        Mockito.`when`(sunriseDao.fetchOlderSunrise()).thenReturn(sunriseEntity)

        sunriseRoomDataSource.saveSunrise(sunrise)
        Mockito.verify(sunriseDao).deleteSunrise(sunriseEntity)
    }
}