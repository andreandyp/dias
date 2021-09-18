package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

@JsonClass(generateAdapter = true)
data class SunriseNetwork(
    val results: ResultsNetwork,
    val status: String
)

fun SunriseNetwork.asDomain(): Sunrise {
    val offsetDateTime = OffsetDateTime.parse(results.sunrise)
    val dateTimeUTC = offsetDateTime.toZonedDateTime()
    return Sunrise(
        dayOfWeek = dateTimeUTC.dayOfWeek,
        dateTimeUTC = dateTimeUTC.withSecond(0),
        origin = Origin.INTERNET
    )
}