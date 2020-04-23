package me.andreandyp.dias.bd

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.andreandyp.dias.bd.dao.AmanecerDAO
import me.andreandyp.dias.bd.entities.AmanecerEntity
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

    /**
     * Obtiene el amanecer de mañana de la BD.
     * Si no está en la BD, se obtiene de la API.
     * @param latitud [String] la latitud de la ubicación del dispositivo
     * @param longitud [String] la longitud de la ubicación del dispositivo
     */
    suspend fun obtenerAmanecerDiario(latitud: String, longitud: String): Amanecer {
        val amanecerBD = obtenerAmanecerBD()
        if (amanecerBD == null) {
            val amanecerAPI = obtenerAmanecerAPI(latitud, longitud)
            insertarAmanecer(amanecerAPI)
            return amanecerAPI.asDomain()
        }

        return amanecerBD.asDomain()
    }

    /**
     * Obtiene el amanecer de mañana desde la BD.
     * @return el amanecer en forma de [AmanecerEntity] o null si no hay ningún amanecer
     */
    private suspend fun obtenerAmanecerBD(): AmanecerEntity? {
        val tomorrowDate = LocalDate.now().plusDays(1)
        return withContext(Dispatchers.IO) {
            amanecerDAO.obtenerSiguienteAmanecer(tomorrowDate)
        }
    }

    /**
     * Obtiene el amanecer de mañana desde la API.
     * @return el amanecer en forma de [AmanecerNetwork]
     */
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

    /**
     * Inserta un amanecer en forma de [AmanecerNetwork] en la BD.
     * Elimina el último amanecer en caso de que existan más de 30.
     * @param amanecerNetwork [AmanecerNetwork] de la API.
     */
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