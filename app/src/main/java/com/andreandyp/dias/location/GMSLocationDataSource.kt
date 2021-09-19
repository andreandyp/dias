package com.andreandyp.dias.location

import android.annotation.SuppressLint
import android.location.Location
import com.andreandyp.dias.repository.location.LocationDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GMSLocationDataSource @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : LocationDataSource {
    @SuppressLint("MissingPermission")
    override suspend fun fetchLastLocation(): Location? {
        return fusedLocationProviderClient.lastLocation.await()
    }
}