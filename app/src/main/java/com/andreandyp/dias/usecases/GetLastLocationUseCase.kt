package com.andreandyp.dias.usecases

import android.location.Location
import com.andreandyp.dias.repository.location.LocationRepository
import javax.inject.Inject

open class GetLastLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    open suspend operator fun invoke(): Location? {
        return locationRepository.fetchLastLocation()
    }
}