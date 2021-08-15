package com.andreandyp.dias.repository.location

import android.location.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(
    private val locationDataSource: LocationDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun fetchLastLocation(): Location? = withContext(dispatcher) {
        locationDataSource.fetchLastLocation()
    }
}