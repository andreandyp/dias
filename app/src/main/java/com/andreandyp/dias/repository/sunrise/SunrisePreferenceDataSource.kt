package com.andreandyp.dias.repository.sunrise

import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise

interface SunrisePreferenceDataSource {
    fun fetchSunrise(origin: Origin): Sunrise
}