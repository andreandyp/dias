package com.andreandyp.dias.repository.location

import android.location.Location
import com.andreandyp.dias.mocks.LocationMocks
import javax.inject.Inject

class FakeLocationRepository @Inject constructor() : LocationRepository {
    override suspend fun fetchLastLocation(): Location = LocationMocks.fakeLocation
}