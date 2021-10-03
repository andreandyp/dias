package com.andreandyp.dias.repository.location

import android.location.Location

interface LocationRepository {
    suspend fun fetchLastLocation(): Location?
}