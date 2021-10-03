package com.andreandyp.dias.repository.location

import android.location.Location
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class LocationRepositoryImplTest {
    private lateinit var locationDataSource: LocationDataSource

    private val location = Location("").apply {
        latitude = 0.0
        longitude = 0.0
    }

    private val repository by lazy {
        LocationRepositoryImpl(locationDataSource)
    }

    @Before
    fun setUp() {
        locationDataSource = mock {
            onBlocking { fetchLastLocation() } doReturn location
        }
    }

    @Test
    fun `fetches last location from data source`() = runBlocking {
        val location = repository.fetchLastLocation()
        verify(locationDataSource).fetchLastLocation()
        assertThat(location).isInstanceOf(Location::class.java)
    }
}