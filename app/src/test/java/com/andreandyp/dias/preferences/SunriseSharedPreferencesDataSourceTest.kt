package com.andreandyp.dias.preferences

import android.content.SharedPreferences
import com.andreandyp.dias.domain.Origin
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

class SunriseSharedPreferencesDataSourceTest {
    private val hourToReturn = "12:00"
    private val defaultValue = "07:00"
    private val sharedPreferences: SharedPreferences = mock {
        on { getString(any(), eq(defaultValue)) } doReturn hourToReturn
    }

    private val tomorrowDate = ZonedDateTime.now().plusDays(1)

    private lateinit var sunriseSharedPreferencesDataSource: SunriseSharedPreferencesDataSource

    @Before
    fun setUp() {
        sunriseSharedPreferencesDataSource = SunriseSharedPreferencesDataSource(sharedPreferences)
    }

    @Test
    fun `fetches a sunrise successfully`() {
        val hour = LocalTime.parse(hourToReturn)
        val dateTimeUTC = tomorrowDate.withHour(hour[ChronoField.HOUR_OF_DAY])
            .withMinute(hour[ChronoField.MINUTE_OF_HOUR])
            .withSecond(0)
            .withNano(0)

        val sunrise = sunriseSharedPreferencesDataSource.fetchSunrise(Origin.DATABASE)

        verify(sharedPreferences).getString(any(), eq(defaultValue))
        assertThat(sunrise.dayOfWeek).isEqualTo(tomorrowDate.dayOfWeek)
        assertThat(sunrise.dateTimeUTC).isEqualTo(dateTimeUTC)
        assertThat(sunrise.origin).isEqualTo(Origin.DATABASE)
    }
}