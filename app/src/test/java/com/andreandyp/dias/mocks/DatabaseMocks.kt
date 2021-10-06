package com.andreandyp.dias.mocks

import com.andreandyp.dias.bd.entities.SunriseEntity
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

object DatabaseMocks {
    val sunriseEntity = SunriseEntity(
        sunriseDate = LocalDate.parse("2021-01-01"),
        sunriseTime = LocalTime.parse("00:00"),
    )
    val sunriseFromEntity = Sunrise(
        dayOfWeek = DayOfWeek.MONDAY,
        dateTimeUTC = ZonedDateTime.parse("2021-01-01T00:00:00+00:00"),
        origin = Origin.DATABASE
    )
}