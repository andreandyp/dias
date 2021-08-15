package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.entities.asDomain
import com.andreandyp.dias.bd.entities.asEntity
import com.andreandyp.dias.domain.Sunrise
import com.andreandyp.dias.repository.sunrise.SunriseLocalDataSource
import java.time.LocalDate

class SunriseRoomDataSource(diasDatabase: DiasDatabase) : SunriseLocalDataSource {
    private val sunriseDao = diasDatabase.sunriseDao()

    override suspend fun fetchSunrise(tomorrowDate: LocalDate): Sunrise? {
        return sunriseDao.fetchSunrise(tomorrowDate)?.asDomain()
    }

    override suspend fun saveSunrise(sunrise: Sunrise) {
        val sunrises = sunriseDao.countSavedSunrises()
        if (sunrises >= MAX_SUNRISES_ALLOWED) {
            val olderSunrise = sunriseDao.fetchOlderSunrise()
            sunriseDao.deleteSunrise(olderSunrise)
            return
        }

        val tomorrowDate = LocalDate.now().plusDays(1)
        val nextSunrise = sunriseDao.fetchSunrise(tomorrowDate)
        if (nextSunrise != null) {
            return
        }

        sunriseDao.saveSunrise(sunrise.asEntity())
    }

    companion object {
        private const val MAX_SUNRISES_ALLOWED = 30
    }
}