package com.andreandyp.dias.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class GMSLocationDataSourceTest {
    private val task: Task<Location> = mock {
        on { isComplete } doReturn true
        on { isCanceled } doReturn false
        on { result } doReturn fakeLocation
    }
    private val fusedLocationProviderClient: FusedLocationProviderClient = mock {
        on { lastLocation } doReturn task
    }
    private val fakeLocation = Location("").apply {
        latitude = 1.0
        longitude = 1.0
    }

    private lateinit var gmsLocationDataSource: GMSLocationDataSource

    @Before
    fun setUp() {
        gmsLocationDataSource = GMSLocationDataSource(fusedLocationProviderClient)
    }

    @Test
    fun `calls fused location provider client`() = runBlocking {
        gmsLocationDataSource.fetchLastLocation()
        verify(fusedLocationProviderClient).lastLocation
        Unit
    }
}