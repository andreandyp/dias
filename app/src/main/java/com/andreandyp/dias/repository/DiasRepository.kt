package com.andreandyp.dias.repository

import android.content.Context
import android.location.Location
import com.andreandyp.dias.bd.DiasDatabase
import com.andreandyp.dias.bd.dao.AmanecerDAO
import com.andreandyp.dias.bd.entities.AmanecerEntity
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.domain.Amanecer
import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.network.AmanecerNetwork
import com.andreandyp.dias.network.SunriseSunsetAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class DiasRepository(
    val context: Context,
    private val sharedPreferencesDataSource: PreferencesDataSource,
) {
    private val amanecerDAO: AmanecerDAO

    init {
        val db = DiasDatabase.getDatabase(context)
        amanecerDAO = db.tiempoDao()
    }

    /**
     * Obtiene el amanecer de mañana de la BD.
     * Si no está en la BD, se obtiene de la API.
     * Si no hay internet, se obtiene el amanecer predefinido por el usuario.
     * Si la ubicación no está activada, se obtiene el amanecer predefinido por el usuario.
     * @param ubicacion [Location] la ubicacion del dispositivo.
     * @param forzarActualizacion [Boolean] si es true, se obtendrán los datos de internet aunque ya estén en la base.
     */
    suspend fun obtenerAmanecerDiario(
        ubicacion: Location?,
        forzarActualizacion: Boolean
    ): Amanecer {
        return withContext(Dispatchers.IO) {
            if (!forzarActualizacion) {
                val amanecerBD = obtenerAmanecerBD()
                if (amanecerBD != null) {
                    return@withContext amanecerBD.asDomain(Origen.BD)
                }
            }

            if (ubicacion != null) {
                try {
                    val amanecerAPI = obtenerAmanecerAPI(
                        ubicacion.latitude.toString(),
                        ubicacion.longitude.toString()
                    )
                    insertarAmanecer(amanecerAPI)
                    return@withContext amanecerAPI.asDomain(Origen.INTERNET)
                } catch (e: Exception) {
                    return@withContext obtenerAmanecerUsuario(Origen.USUARIO_NORED)
                }
            }

            return@withContext obtenerAmanecerUsuario(Origen.USUARIO_NOUBIC)
        }
    }

    /**
     * Obtiene el amanecer de mañana desde la BD.
     * @return el amanecer en forma de [AmanecerEntity] o null si no hay ningún amanecer
     */
    private suspend fun obtenerAmanecerBD(): AmanecerEntity? {
        val tomorrowDate = LocalDate.now().plusDays(1)
        return withContext(Dispatchers.IO) {
            amanecerDAO.obtenerAmanecer(tomorrowDate)
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

    private fun obtenerAmanecerUsuario(origen: Origen): Amanecer {
        return sharedPreferencesDataSource.obtenerAmanecerUsuario(origen)
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
                return@withContext
            }

            val tomorrowDate = LocalDate.now().plusDays(1)
            val amanecerDeHoy = amanecerDAO.obtenerAmanecer(tomorrowDate)
            if (amanecerDeHoy != null) {
                return@withContext
            }

            amanecerDAO.insertarAmanecer(amanecerNetwork.asEntity())
        }
    }

    fun establecerPreferenciasAlarma(alarma: Alarma): Alarma {
        return sharedPreferencesDataSource.establecerPreferenciasAlarma(alarma)
    }

    fun guardarEstadoAlarma(alarma: Alarma) =
        sharedPreferencesDataSource.guardarPreferencias("${alarma.id}_on", alarma.encendida)

    /**
     * Guarda la configuración de vibración de la alarma (sí/no).
     */
    fun guardarVibrarAlarma(alarma: Alarma) =
        sharedPreferencesDataSource.guardarPreferencias("${alarma.id}_vib", alarma.vibrar)

    /**
     * Guarda la hora a la que sonará la alarma.
     */
    fun guardarHorasAlarma(alarma: Alarma) =
        sharedPreferencesDataSource.guardarPreferencias("${alarma.id}_hr", alarma.horasDiferencia)

    /**
     * Guarda los minutos a los que sonará la alarma.
     */
    fun guardarMinAlarma(alarma: Alarma) =
        sharedPreferencesDataSource.guardarPreferencias(
            "${alarma.id}_min",
            alarma.minutosDiferencia
        )

    /**
     * Guarda el momento en el que sonará la alarma (antes/después del amanecer).
     */
    fun guardarMomentoAlarma(alarma: Alarma) =
        sharedPreferencesDataSource.guardarPreferencias("${alarma.id}_momento", alarma.momento)

    fun guardarTonoAlarma(alarma: Alarma) =
        sharedPreferencesDataSource.guardarPreferencias("${alarma.id}_tono", alarma.tono ?: "")

    fun guardarUriTonoAlarma(alarma: Alarma) =
        sharedPreferencesDataSource.guardarPreferencias("${alarma.id}_uri", alarma.uriTono ?: "")
}