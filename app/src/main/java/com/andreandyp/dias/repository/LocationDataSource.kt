package com.andreandyp.dias.repository

import android.location.Location

interface LocationDataSource {
    suspend fun obtenerUbicacion(): Location?
}