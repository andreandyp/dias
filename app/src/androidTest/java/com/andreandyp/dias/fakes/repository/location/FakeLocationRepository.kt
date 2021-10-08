package com.andreandyp.dias.fakes.repository.location

import android.location.Location
import com.andreandyp.dias.mocks.LocationMocks
import com.andreandyp.dias.repository.location.LocationRepository
import javax.inject.Inject

class FakeLocationRepository @Inject constructor() : LocationRepository {
    override suspend fun fetchLastLocation(): Location = LocationMocks.fakeLocation
}