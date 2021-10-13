package com.andreandyp.dias.mocks

import com.andreandyp.dias.bd.entities.SunriseEntity
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

object DatabaseMocks {
    private val tomorrowDate = ZonedDateTime.of(
        LocalDateTime.now().plusDays(1),
        ZoneOffset.ofOffset("", ZoneOffset.UTC),
    )
    val sunriseEntity = SunriseEntity(
        sunriseDate = tomorrowDate.toLocalDate(),
        sunriseTime = tomorrowDate.toLocalTime(),
    )
    val sunriseFromEntity = Sunrise(
        dayOfWeek = tomorrowDate.dayOfWeek,
        dateTimeUTC = tomorrowDate,
        origin = Origin.DATABASE
    )
}