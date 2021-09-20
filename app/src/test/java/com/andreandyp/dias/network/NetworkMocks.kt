package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.DayOfWeek
import java.time.ZonedDateTime

object NetworkMocks {
    val sunriseNetwork = SunriseNetwork(
        status = "Ok",
        results = ResultsNetwork(
            sunrise = "2021-09-18T12:23:09+00:00",
            sunset = "2021-09-19T00:36:45+00:00",
            solarNoon = "2021-09-18T18:29:57+00:00",
            dayLength = 44016,
            civilTwilightBegin = "2021-09-18T12:02:23+00:00",
            civilTwilightEnd = "2021-09-19T00:57:31+00:00",
            nauticalTwilightBegin = "2021-09-18T11:36:55+00:00",
            nauticalTwilightEnd = "2021-09-19T01:22:59+00:00",
            astronomicalTwilightBegin = "2021-09-18T11:11:20+00:00",
            astronomicalTwilightEnd = "2021-09-19T01:48:34+00:00",
        )
    )

    val sunrise = Sunrise(
        dateTimeUTC = ZonedDateTime.parse("2021-09-18T12:23:09+00:00").withSecond(0),
        dayOfWeek = DayOfWeek.SATURDAY,
        origin = Origin.INTERNET
    )
}