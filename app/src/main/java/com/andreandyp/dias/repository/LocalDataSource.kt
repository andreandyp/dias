package com.andreandyp.dias.repository

import com.andreandyp.dias.domain.Amanecer
import org.threeten.bp.LocalDate

interface LocalDataSource {
    /**
     * Obtiene el amanecer desde la fuente local.
     * @param [tomorrowDate] la fecha del día siguiente.
     * @return el amanecer de mañana.
     */
    suspend fun obtenerAmanecer(tomorrowDate: LocalDate): Amanecer?

    /**
     * Guarda el amanecer en la fuente local.
     * @param [amanecer] amanecer de mañana.
     */
    suspend fun insertarAmanecer(amanecer: Amanecer)
}