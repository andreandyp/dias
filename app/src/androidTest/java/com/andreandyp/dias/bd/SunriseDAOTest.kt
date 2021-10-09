package com.andreandyp.dias.bd

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.andreandyp.dias.bd.entities.SunriseEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class SunriseDAOTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val tomorrowDate = LocalDateTime.now().plusDays(1)

    private lateinit var database: DiasDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DiasDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun savesASunriseAndGetsByDate() = runBlocking {
        val sunriseToInsert = SunriseEntity(
            sunriseTime = tomorrowDate.toLocalTime(),
            sunriseDate = tomorrowDate.toLocalDate(),
        )
        database.sunriseDao().insertSunrise(sunriseToInsert)

        val sunriseLoaded = database.sunriseDao().fetchSunrise(tomorrowDate.toLocalDate())

        assertThat(sunriseLoaded).isNotNull()
        assertThat(sunriseToInsert.sunriseDate).isEqualTo(sunriseLoaded!!.sunriseDate)
        assertThat(sunriseToInsert.sunriseTime).isEqualTo(sunriseLoaded.sunriseTime)
    }

    @Test
    fun countsSavedSunrises() = runBlocking {
        val limit = 10
        (1..limit).forEach { _ ->
            val sunrise = SunriseEntity(
                sunriseTime = tomorrowDate.toLocalTime(),
                sunriseDate = tomorrowDate.toLocalDate(),
            )
            database.sunriseDao().insertSunrise(sunrise)
        }

        val savedSunrises = database.sunriseDao().countSavedSunrises()

        assertThat(savedSunrises).isEqualTo(limit)
    }

    @Test
    fun fetchesOlderSunrise() = runBlocking {
        val todayDate = LocalDateTime.now().plusDays(1)
        val sunriseToday = SunriseEntity(
            sunriseTime = todayDate.toLocalTime(),
            sunriseDate = todayDate.toLocalDate(),
        )
        val sunriseTomorrow = SunriseEntity(
            sunriseTime = tomorrowDate.toLocalTime(),
            sunriseDate = tomorrowDate.toLocalDate(),
        )

        database.sunriseDao().insertSunrise(sunriseToday)
        database.sunriseDao().insertSunrise(sunriseTomorrow)

        val olderSunrise = database.sunriseDao().fetchOlderSunrise()
        assertThat(olderSunrise).isNotNull()
        assertThat(olderSunrise!!.sunriseDate).isEqualTo(sunriseToday.sunriseDate)
        assertThat(olderSunrise.sunriseTime).isEqualTo(sunriseToday.sunriseTime)
    }

    @Test
    fun deletesASunrise() = runBlocking {
        val sunriseTomorrow = SunriseEntity(
            sunriseTime = tomorrowDate.toLocalTime(),
            sunriseDate = tomorrowDate.toLocalDate(),
        )

        val insertedId = database.sunriseDao().insertSunrise(sunriseTomorrow)
        val sunriseToDelete = sunriseTomorrow.copy(_id = insertedId)
        database.sunriseDao().deleteSunrise(sunriseToDelete)

        val sunriseLoaded = database.sunriseDao().fetchSunrise(tomorrowDate.toLocalDate())
        val currentSunrises = database.sunriseDao().countSavedSunrises()
        assertThat(sunriseLoaded).isNull()
        assertThat(currentSunrises).isEqualTo(0)
    }

    @After
    fun tearDown() = database.close()
}