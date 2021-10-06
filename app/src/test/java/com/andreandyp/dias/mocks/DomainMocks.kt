package com.andreandyp.dias.mocks

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.ZonedDateTime

object DomainMocks {
    val sunriseNetwork = Sunrise(
        dayOfWeek = ZonedDateTime.now().dayOfWeek,
        dateTimeUTC = ZonedDateTime.now().plusDays(1).withSecond(0).withNano(0),
        origin = Origin.INTERNET,
    )
    val sunriseLocal = Sunrise(
        dayOfWeek = ZonedDateTime.now().dayOfWeek,
        dateTimeUTC = ZonedDateTime.now().plusDays(1).withSecond(0).withNano(0),
        origin = Origin.DATABASE,
    )
    val alarm = Alarm(1, true)
}