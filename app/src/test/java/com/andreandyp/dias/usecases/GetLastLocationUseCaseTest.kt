package com.andreandyp.dias.usecases

import android.location.Location
import com.andreandyp.dias.repository.location.LocationRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GetLastLocationUseCaseTest {

    private lateinit var locationRepository: LocationRepository

    private val fakeLocation = Location("")

    private val getLastLocationUseCase by lazy {
        GetLastLocationUseCase(locationRepository)
    }

    @Before
    fun setUp() {
        locationRepository = mock {
            onBlocking { fetchLastLocation() } doReturn fakeLocation
        }
    }

    @Test
    fun `returns last location`() = runBlocking {
        val location = getLastLocationUseCase()
        verify(locationRepository).fetchLastLocation()
        assertThat(location).isEqualTo(fakeLocation)
    }
}