package com.andreandyp.dias.repository.location

import android.location.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LocationRepository {
    override suspend fun fetchLastLocation(): Location? = withContext(dispatcher) {
        locationDataSource.fetchLastLocation()
    }
}