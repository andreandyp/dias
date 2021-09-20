package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Test

class NetworkMappersTest {
    @Test
    fun `transforms network dto to ddo`() {
        val ddo = NetworkMocks.sunriseNetwork.asDomain()
        assertThat(ddo, isA(Sunrise::class.java))
        assertThat(ddo.origin, IsEqual(Origin.INTERNET))
        assertThat(ddo.dateTimeUTC, IsEqual(NetworkMocks.sunrise.dateTimeUTC))
        assertThat(ddo.dayOfWeek, IsEqual(NetworkMocks.sunrise.dayOfWeek))
    }
}