package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.dao.SunriseDAO
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.io.IOException
import java.time.LocalDate

class SunriseRoomDataSourceTest {
    private val sunriseEntity = DatabaseMocks.sunriseEntity
    private val sunrise = DatabaseMocks.sunrise
    private val sunriseDao: SunriseDAO = mock {
        onBlocking {
            fetchSunrise(any())
        } doReturn sunriseEntity
    }

    private lateinit var sunriseRoomDataSource: SunriseRoomDataSource

    @Before
    fun setup() {
        sunriseRoomDataSource = SunriseRoomDataSource(sunriseDao)
    }

    @Test
    fun `fetches data from database successfully`() = runBlocking {
        val localDate = LocalDate.now().plusDays(1)
        sunriseRoomDataSource.fetchSunrise(localDate)

        verify(sunriseDao).fetchSunrise(localDate)
        Unit
    }

    @Test
    fun `throws an exception when fails to fetch`() = runBlocking {
        sunriseDao.apply {
            whenever(fetchSunrise(any())) doAnswer { throw IOException() }
        }
        val localDate = LocalDate.now().plusDays(1)
        val exception = try {
            sunriseRoomDataSource.fetchSunrise(localDate)
            null
        } catch (e: Exception) {
            e
        }

        verify(sunriseDao).fetchSunrise(localDate)
        assertThat(exception).isInstanceOf(IOException::class.java)
    }

    @Test
    fun `saves a sunrise successfully`() = runBlocking {
        whenever(sunriseDao.countSavedSunrises()) doReturn 0
        whenever(sunriseDao.fetchSunrise(any())) doReturn null

        sunriseRoomDataSource.saveSunrise(sunrise)

        verify(sunriseDao).saveSunrise(sunriseEntity)
    }

    @Test
    fun `removes a sunrise when sunrises more 30`() = runBlocking {
        whenever(sunriseDao.countSavedSunrises()) doReturn 30
        whenever(sunriseDao.fetchOlderSunrise()) doReturn sunriseEntity

        sunriseRoomDataSource.saveSunrise(sunrise)

        verify(sunriseDao).deleteSunrise(sunriseEntity)
    }
}