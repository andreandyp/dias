package com.andreandyp.dias.location

import android.annotation.SuppressLint
import android.location.Location
import com.andreandyp.dias.repository.LocationDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await

class GMSLocationDataSource(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : LocationDataSource {
    @SuppressLint("MissingPermission")
    override suspend fun obtenerUbicacion(): Location? {
        return fusedLocationProviderClient.lastLocation.await()
    }
}