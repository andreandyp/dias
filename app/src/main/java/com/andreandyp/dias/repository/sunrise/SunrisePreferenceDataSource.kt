package com.andreandyp.dias.repository.sunrise

import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.domain.Sunrise

interface SunrisePreferenceDataSource {
    fun fetchSunrise(origin: Origen): Sunrise
}