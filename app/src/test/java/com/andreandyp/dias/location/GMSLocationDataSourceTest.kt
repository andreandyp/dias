package com.andreandyp.dias.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GMSLocationDataSourceTest {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var task: Task<Location>
    private val fakeLocation = Location("").apply {
        latitude = 1.0
        longitude = 1.0
    }

    private val gmsLocationDataSource by lazy {
        GMSLocationDataSource(fusedLocationProviderClient)
    }

    @Before
    fun setUp() {
        task = mock {
            on { isComplete } doReturn true
            on { isCanceled } doReturn false
            on { result } doReturn fakeLocation
        }
        fusedLocationProviderClient = mock {
            on { lastLocation } doReturn task
        }
    }

    @Test
    fun a() = runBlocking {
        val location = gmsLocationDataSource.fetchLastLocation()
        verify(fusedLocationProviderClient).lastLocation
        assertThat(fakeLocation).isEqualTo(location)
    }
}