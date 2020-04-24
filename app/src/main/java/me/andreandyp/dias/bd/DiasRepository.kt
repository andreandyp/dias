package me.andreandyp.dias.bd

import android.content.Context
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.andreandyp.dias.bd.dao.AmanecerDAO
import me.andreandyp.dias.bd.entities.AmanecerEntity
import me.andreandyp.dias.domain.Amanecer
import me.andreandyp.dias.network.AmanecerNetwork
import me.andreandyp.dias.network.SunriseSunsetAPI
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

class DiasRepository(context: Context) {
    private val amanecerDAO: AmanecerDAO

    init {
        val db = DiasDatabase.getDatabase(context)
        amanecerDAO = db.tiempoDao()
    }

    /**
     * Obtiene el amanecer de mañana de la BD.
     * Si no está en la BD, se obtiene de la API.
     * Si la ubicación no está activada, se obtiene desde los datos del usuario
     * @param ubicacion [Location] la ubicacion del dispositivo
     */
    suspend fun obtenerAmanecerDiario(ubicacion: Location?): Amanecer {
        return withContext(Dispatchers.IO) {
            val amanecerBD = obtenerAmanecerBD()
            if(amanecerBD != null){
                return@withContext amanecerBD.asDomain()
            }

            if (ubicacion != null) {
                val amanecerAPI = obtenerAmanecerAPI(
                    ubicacion.latitude.toString(),
                    ubicacion.longitude.toString()
                )
                insertarAmanecer(amanecerAPI)
                return@withContext amanecerAPI.asDomain()
            }

            // Si no hay amanecer en la BD, ni ubicación, se utiliza los ajustes del usuario
            val tomorrowDate = ZonedDateTime.now(ZoneId.systemDefault())
                .plusDays(1)
                .withHour(7)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
            return@withContext Amanecer(
                diaSemana = tomorrowDate[ChronoField.DAY_OF_WEEK],
                fechaHoraLocal = tomorrowDate,
                deInternet = false
            )
        }
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