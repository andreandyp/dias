package com.andreandyp.dias.repository

import com.andreandyp.dias.domain.Amanecer
import org.threeten.bp.LocalDate

interface RemoteDataSource {
    /**
     * Obtiene el amanecer de mañana desde la API.
     * @return el amanecer en forma de [Amanecer]
     */
    suspend fun obtenerAmanecerAPI(
        tomorrowDate: LocalDate,
        latitud: String,
        longitud: String
    ): Amanecer
}