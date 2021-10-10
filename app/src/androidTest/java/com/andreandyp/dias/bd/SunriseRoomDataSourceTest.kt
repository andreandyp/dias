package com.andreandyp.dias.bd

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.andreandyp.dias.bd.SunriseRoomDataSource.Companion.MAX_SUNRISES_ALLOWED
import com.andreandyp.dias.bd.dao.SunriseDAO
import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.mocks.DatabaseMocks
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SunriseRoomDataSourceTest {
    private lateinit var database: DiasDatabase
    private lateinit var sunriseRoomDataSource: SunriseRoomDataSource
    private lateinit var sunriseDao: SunriseDAO

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val sunrise = DatabaseMocks.sunriseFromEntity
    private val sunriseEntity = DatabaseMocks.sunriseEntity
    private val tomorrowDate = sunriseEntity.sunriseDate

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DiasDatabase::class.java,
        ).allowMainThreadQueries().build()
        sunriseDao = database.sunriseDao()
        sunriseRoomDataSource = SunriseRoomDataSource(sunriseDao)
    }

    @Test
    fun fetchesDataFromDatabaseSuccessfully() = runBlocking {
        sunriseRoomDataSource.saveSunrise(sunrise)
        val sunrise = sunriseRoomDataSource.fetchSunrise(tomorrowDate)

        assertThat(sunrise).isInstanceOf(Sunrise::class.java)
    }

    @Test
    fun savesASunriseSuccessfully() = runBlocking {
        sunriseRoomDataSource.saveSunrise(sunrise)

        val sunriseInserted = sunriseRoomDataSource.fetchSunrise(tomorrowDate)
        assertThat(sunrise).isEqualTo(sunriseInserted)
    }

    @Test
    fun removesASunriseWhenSunrisesAreMoreThan30() = runBlocking {
        (1..MAX_SUNRISES_ALLOWED).forEach { _ ->
            sunriseDao.insertSunrise(sunriseEntity)
        }

        sunriseRoomDataSource.saveSunrise(sunrise)

        val sunriseInserted = sunriseRoomDataSource.fetchSunrise(tomorrowDate)
        assertThat(sunrise).isEqualTo(sunriseInserted)
    }

    @Test
    fun doesNotSavePreviousSavedSunrise() = runBlocking {
        sunriseDao.insertSunrise(sunriseEntity)

        sunriseRoomDataSource.saveSunrise(sunrise)

        val savedSunrises = sunriseDao.countSavedSunrises()
        assertThat(savedSunrises).isEqualTo(1)
    }

    @After
    fun tearDown() {
        database.close()
    }
}