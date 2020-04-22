package me.andreandyp.dias.bd

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.andreandyp.dias.bd.dao.AmanecerDAO
import me.andreandyp.dias.domain.Amanecer
import me.andreandyp.dias.network.AmanecerNetwork
import me.andreandyp.dias.network.SunriseSunsetAPI
import org.threeten.bp.LocalDate

class DiasRepository(application: Application) {
    private val amanecerDAO: AmanecerDAO

    init {
        val db = DiasDatabase.getDatabase(application.applicationContext)
        amanecerDAO = db.tiempoDao()
    }

    suspend fun obtenerAmanecerDiario(latitud: String, longitud: String): Amanecer {
        val amanecer = obtenerAmanecerAPI(latitud, longitud)
        insertarAmanecer(amanecer)

        return amanecer.asDomain()
    }


    private suspend fun obtenerAmanecerAPI(latitud: String, longitud: String): AmanecerNetwork {
        val tomorrowDate = LocalDate.now().plusDays(1)
        return withContext(Dispatchers.IO) {
            SunriseSunsetAPI.sunriseSunsetService.obtenerAmanecer(
                latitud,
                longitud,
                tomorrowDate.toString()
            )
        }
    }

    private suspend fun insertarAmanecer(amanecerNetwork: AmanecerNetwork) {
        withContext(Dispatchers.IO) {
            val amaneceres = amanecerDAO.obtenerNumeroAmaneceres()
            if (amaneceres >= 30) {
                val masAntiguo = amanecerDAO.obtenerAmanecerMasAntiguo()
                amanecerDAO.eliminarAmanecer(masAntiguo)
            } else {
                amanecerDAO.insertarAmanecer(amanecerNetwork.asEntity())
            }
        }
    }
}