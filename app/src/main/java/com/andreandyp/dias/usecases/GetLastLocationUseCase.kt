package com.andreandyp.dias.usecases

import android.location.Location
import com.andreandyp.dias.repository.location.LocationRepository
import javax.inject.Inject

class GetLastLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Location? {
        return locationRepository.fetchLastLocation()
    }
}