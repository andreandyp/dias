package com.andreandyp.dias.repository.location

import android.location.Location
import com.andreandyp.dias.mocks.LocationMocks
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class LocationRepositoryImplTest {
    private val fakeLocation = LocationMocks.fakeLocation

    private val locationDataSource: LocationDataSource = mock {
        onBlocking { fetchLastLocation() } doReturn fakeLocation
    }

    private lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        repository = LocationRepositoryImpl(locationDataSource)
    }

    @Test
    fun `fetches last location from data source`() = runBlocking {
        val location = repository.fetchLastLocation()
        verify(locationDataSource).fetchLastLocation()
        assertThat(location).isInstanceOf(Location::class.java)
    }
}