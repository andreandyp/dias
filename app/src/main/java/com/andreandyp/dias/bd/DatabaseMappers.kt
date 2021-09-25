package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.entities.SunriseEntity
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun SunriseEntity.asDomain(): Sunrise {
    val dateTimeUTC = ZonedDateTime.of(
        sunriseDate,
        sunriseTime,
        ZoneOffset.ofOffset("", ZoneOffset.UTC)
    )

    return Sunrise(
        dayOfWeek = dateTimeUTC.dayOfWeek,
        dateTimeUTC = dateTimeUTC,
        origin = Origin.DATABASE
    )
}

fun Sunrise.asEntity(): SunriseEntity {
    val sunriseDate = dateTimeUTC.toLocalDate()
    val sunriseTime = dateTimeUTC.toLocalTime()
    return SunriseEntity(
        sunriseDate = sunriseDate,
        sunriseTime = sunriseTime,
    )
}