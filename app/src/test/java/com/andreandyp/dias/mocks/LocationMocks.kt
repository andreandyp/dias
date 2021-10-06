package com.andreandyp.dias.mocks

import android.location.Location

object LocationMocks {
    const val fakeLatitude = 1.0
    const val fakeLongitude = 1.0
    val fakeLocation = Location("").apply {
        latitude = fakeLatitude
        longitude = fakeLongitude
    }
}