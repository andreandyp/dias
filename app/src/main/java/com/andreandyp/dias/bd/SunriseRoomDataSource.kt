package com.andreandyp.dias.bd

import androidx.annotation.VisibleForTesting
import com.andreandyp.dias.bd.dao.SunriseDAO
import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.repository.sunrise.SunriseLocalDataSource
import java.lang.reflect.Modifier.PRIVATE
import java.time.LocalDate
import javax.inject.Inject

class SunriseRoomDataSource @Inject constructor(
    private val sunriseDao: SunriseDAO
) : SunriseLocalDataSource {

    override suspend fun fetchSunrise(tomorrowDate: LocalDate): Sunrise? {
        return sunriseDao.fetchSunrise(tomorrowDate)?.asDomain()
    }

    override suspend fun saveSunrise(sunrise: Sunrise) {
        val sunrises = sunriseDao.countSavedSunrises()
        if (sunrises >= MAX_SUNRISES_ALLOWED) {
            val olderSunrise = sunriseDao.fetchOlderSunrise()
            sunriseDao.deleteSunrise(olderSunrise!!)
            return
        }

        val tomorrowDate = LocalDate.now().plusDays(1)
        val nextSunrise = sunriseDao.fetchSunrise(tomorrowDate)
        if (nextSunrise != null) {
            return
        }

        sunriseDao.insertSunrise(sunrise.asEntity())
    }

    companion object {
        @VisibleForTesting(otherwise = PRIVATE)
        const val MAX_SUNRISES_ALLOWED = 30
    }
}