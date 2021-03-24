package com.andreandyp.dias.network

import com.andreandyp.dias.domain.Amanecer
import com.andreandyp.dias.domain.asDomain
import com.andreandyp.dias.repository.RemoteDataSource
import org.threeten.bp.LocalDate

class RetrofitDataSource(private val sunriseSunsetService: SunriseSunsetService) :
    RemoteDataSource {

    override suspend fun obtenerAmanecerAPI(
        tomorrowDate: LocalDate,
        latitud: String,
        longitud: String
    ): Amanecer {
        return sunriseSunsetService.obtenerAmanecer(
            latitud,
            longitud,
            tomorrowDate.toString()
        ).asDomain()
    }
}