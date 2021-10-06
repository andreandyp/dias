package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.entities.SunriseEntity
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DatabaseMappersTest {
    private val sunriseEntity = DatabaseMocks.sunriseEntity
    private val dateTimeUTC = ZonedDateTime.of(
        sunriseEntity.sunriseDate,
        sunriseEntity.sunriseTime,
        ZoneOffset.ofOffset("", ZoneOffset.UTC)
    )

    @Test
    fun `transforms database dto to ddo`() {
        val ddo = sunriseEntity.asDomain()
        assertThat(ddo).isInstanceOf(Sunrise::class.java)
        assertThat(ddo.origin).isEqualTo(Origin.DATABASE)
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