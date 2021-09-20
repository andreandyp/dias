package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.OffsetDateTime

fun SunriseNetwork.asDomain(): Sunrise {
    val offsetDateTime = OffsetDateTime.parse(results.sunrise)
    val dateTimeUTC = offsetDateTime.toZonedDateTime()
    return Sunrise(
        dayOfWeek = dateTimeUTC.dayOfWeek,
        dateTimeUTC = dateTimeUTC.withSecond(0),
        origin = Origin.INTERNET
    )
}