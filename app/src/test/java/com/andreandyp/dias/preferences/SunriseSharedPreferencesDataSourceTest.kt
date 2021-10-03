package com.andreandyp.dias.preferences

import android.content.SharedPreferences
import com.andreandyp.dias.domain.Origin
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

class SunriseSharedPreferencesDataSourceTest {

    private lateinit var sharedPreferences: SharedPreferences
    private val hourToReturn = "12:00"
    private val tomorrowDate = ZonedDateTime.now().plusDays(1)

    private val sunriseSharedPreferencesDataSource by lazy {
        SunriseSharedPreferencesDataSource(sharedPreferences)
    }

    @Before
    fun setUp() {
        sharedPreferences = mock {
            on { getString("hora_default", "07:00") } doReturn hourToReturn
        }
    }

    @Test
    fun `fetches a sunrise successfully`() {
        val hour = LocalTime.parse(hourToReturn)
        val dateTimeUTC = tomorrowDate.withHour(hour[ChronoField.HOUR_OF_DAY])
            .withMinute(hour[ChronoField.MINUTE_OF_HOUR])
            .withSecond(0)
            .withNano(0)

        val sunrise = sunriseSharedPreferencesDataSource.fetchSunrise(Origin.DATABASE)

        assertThat(sunrise.dayOfWeek).isEqualTo(tomorrowDate.dayOfWeek)
        assertThat(sunrise.dateTimeUTC).isEqualTo(dateTimeUTC)
        assertThat(sunrise.origin).isEqualTo(Origin.DATABASE)
    }
}