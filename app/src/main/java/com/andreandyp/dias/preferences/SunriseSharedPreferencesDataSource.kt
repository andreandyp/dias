package com.andreandyp.dias.preferences

import android.content.SharedPreferences
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.repository.sunrise.SunrisePreferenceDataSource
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

class SunriseSharedPreferencesDataSource(
    private val preferences: SharedPreferences
) : SunrisePreferenceDataSource {

    override fun fetchSunrise(origin: Origin): Sunrise {
        val horaPref = LocalTime.parse(preferences.getString("hora_default", DEFAULT_VALUE))
        val tomorrowDate = ZonedDateTime.now(ZoneId.systemDefault())
            .plusDays(1)
            .withHour(horaPref[ChronoField.HOUR_OF_DAY])
            .withMinute(horaPref[ChronoField.MINUTE_OF_HOUR])
            .withSecond(0)
            .withNano(0)
        return Sunrise(
            dayOfWeek = tomorrowDate.dayOfWeek,
            dateTimeUTC = tomorrowDate,
            origin = origin
        )
    }

    companion object {
        private const val DEFAULT_VALUE = "07:00"
    }
}