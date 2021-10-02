package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.entities.SunriseEntity
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DatabaseMappersTest {
    @Test
    fun `transforms database dto to ddo`() {
        val entity = DatabaseMocks.sunriseEntity
        val ddo = entity.asDomain()
        assertThat(ddo).isInstanceOf(Sunrise::class.java)
        assertThat(ddo.origin).isEqualTo(Origin.DATABASE)

        val date = entity.sunriseDate
        val time = entity.sunriseTime
        val dateTimeUTC = ZonedDateTime.of(date, time, ZoneOffset.ofOffset("", ZoneOffset.UTC))
        assertThat(ddo.dateTimeUTC).isEqualTo(dateTimeUTC)
        assertThat(ddo.dayOfWeek).isEqualTo(dateTimeUTC.dayOfWeek)
    }

    @Test
    fun `transforms ddo to database dto`() {
        val dto = DatabaseMocks.sunrise.asEntity()
        assertThat(dto).isInstanceOf(SunriseEntity::class.java)
        assertThat(dto.sunriseDate).isEqualTo(DatabaseMocks.sunrise.dateTimeUTC.toLocalDate())
        assertThat(dto.sunriseTime).isEqualTo(DatabaseMocks.sunrise.dateTimeUTC.toLocalTime())
    }
}