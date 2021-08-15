package com.andreandyp.dias.repository.location

import android.location.Location

interface LocationDataSource {
    suspend fun fetchLastLocation(): Location?
}