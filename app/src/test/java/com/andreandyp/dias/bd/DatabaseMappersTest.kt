package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.entities.SunriseEntity
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DatabaseMappersTest {
    @Test
    fun `transforms database dto to ddo`() {
        val entity = DatabaseMocks.sunriseEntity
        val ddo = entity.asDomain()
        MatcherAssert.assertThat(ddo, CoreMatchers.isA(Sunrise::class.java))
        MatcherAssert.assertThat(ddo.origin, IsEqual(Origin.DATABASE))

        val date = entity.sunriseDate
        val time = entity.sunriseTime
        val dateTimeUTC = ZonedDateTime.of(date, time, ZoneOffset.ofOffset("", ZoneOffset.UTC))
        MatcherAssert.assertThat(ddo.dateTimeUTC, IsEqual(dateTimeUTC))
        MatcherAssert.assertThat(ddo.dayOfWeek, IsEqual(dateTimeUTC.dayOfWeek))
    }

    @Test
    fun `transforms ddo to database dto`() {
        val dto = DatabaseMocks.sunrise.asEntity()
        MatcherAssert.assertThat(dto, CoreMatchers.isA(SunriseEntity::class.java))
        MatcherAssert.assertThat(
            dto.sunriseDate,
            IsEqual(DatabaseMocks.sunrise.dateTimeUTC.toLocalDate())
        )
        MatcherAssert.assertThat(
            dto.sunriseTime,
            IsEqual(DatabaseMocks.sunrise.dateTimeUTC.toLocalTime())
        )
    }
}