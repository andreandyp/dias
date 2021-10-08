package com.andreandyp.dias.mocks

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.LocalDate
import java.time.ZonedDateTime

object PreferencesMocks {
    val sunriseNoInternet = Sunrise(
        dayOfWeek = LocalDate.now().plusDays(1).dayOfWeek,
        dateTimeUTC = ZonedDateTime.now().plusDays(1).withSecond(0),
        origin = Origin.NO_INTERNET,
    )

    val sunriseNoLocation = Sunrise(
        dayOfWeek = LocalDate.now().plusDays(1).dayOfWeek,
        dateTimeUTC = ZonedDateTime.now().plusDays(1).withSecond(0),
        origin = Origin.NO_LOCATION,
    )

    val alarm = Alarm(1, true).apply {
        on = true
        vibration = true
        tone = ""
        uriTone = ""
        offsetHours = 0
        offsetMinutes = 0
        offsetType = 0
        utcRingingAt = ZonedDateTime.now().plusDays(1).withSecond(0).withNano(0)
    }
}