package com.andreandyp.dias.domain

import java.time.DayOfWeek
import java.time.ZonedDateTime

data class Sunrise(
    val dayOfWeek: DayOfWeek,
    val dateTimeUTC: ZonedDateTime,
    val origin: Origin,
)