package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NetworkMappersTest {
    @Test
    fun `transforms network dto to ddo`() {
        val ddo = NetworkMocks.sunriseNetwork.asDomain()
        assertThat(ddo).isInstanceOf(Sunrise::class.java)
        assertThat(ddo.origin).isEqualTo(Origin.INTERNET)
        assertThat(ddo.dateTimeUTC).isEqualTo(NetworkMocks.sunrise.dateTimeUTC)
        assertThat(ddo.dayOfWeek).isEqualTo(NetworkMocks.sunrise.dayOfWeek)
    }
}