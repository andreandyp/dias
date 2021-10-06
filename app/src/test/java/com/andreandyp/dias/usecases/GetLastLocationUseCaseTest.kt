package com.andreandyp.dias.usecases

import android.location.Location
import com.andreandyp.dias.mocks.LocationMocks
import com.andreandyp.dias.repository.location.LocationRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GetLastLocationUseCaseTest {
    private val fakeLocation = LocationMocks.fakeLocation

    private val locationRepository: LocationRepository = mock {
        onBlocking { fetchLastLocation() } doReturn fakeLocation
    }

    private lateinit var getLastLocationUseCase: GetLastLocationUseCase

    @Before
    fun setUp() {
        getLastLocationUseCase = GetLastLocationUseCase(locationRepository)
    }

    @Test
    fun `returns last location`() = runBlocking {
        val location = getLastLocationUseCase()
        verify(locationRepository).fetchLastLocation()
        assertThat(location).isEqualTo(fakeLocation)
    }
}