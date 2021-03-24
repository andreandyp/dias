package com.andreandyp.dias.bd

import com.andreandyp.dias.bd.entities.asEntity
import com.andreandyp.dias.domain.Amanecer
import com.andreandyp.dias.domain.asDomain
import com.andreandyp.dias.repository.LocalDataSource
import org.threeten.bp.LocalDate

class RoomDataSource(diasDatabase: DiasDatabase) : LocalDataSource {
    private val amanecerDAO = diasDatabase.tiempoDao()

    override suspend fun obtenerAmanecer(tomorrowDate: LocalDate): Amanecer? {
        return amanecerDAO.obtenerAmanecer(tomorrowDate)?.asDomain()
    }

    override suspend fun insertarAmanecer(amanecer: Amanecer) {
        val amaneceres = amanecerDAO.obtenerNumeroAmaneceres()
        if (amaneceres >= 30) {
            val masAntiguo = amanecerDAO.obtenerAmanecerMasAntiguo()
            amanecerDAO.eliminarAmanecer(masAntiguo)
            return
        }

        val tomorrowDate = LocalDate.now().plusDays(1)
        val siguienteAmanecer = amanecerDAO.obtenerAmanecer(tomorrowDate)
        if (siguienteAmanecer != null) {
            return
        }

        amanecerDAO.insertarAmanecer(amanecer.asEntity())
    }
}