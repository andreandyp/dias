package com.andreandyp.dias.repository

import android.location.Location
import com.andreandyp.dias.bd.entities.AmanecerEntity
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.domain.Amanecer
import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.network.AmanecerNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class DiasRepository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val preferencesDataSource: PreferencesDataSource,
    private val locationDataSource: LocationDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Obtiene el amanecer de mañana desde la BD.
     * @return el amanecer en forma de [AmanecerEntity] o null si no hay ningún amanecer
     */
    private suspend fun obtenerAmanecerBD(): Amanecer? {
        val tomorrowDate = LocalDate.now().plusDays(1)
        return withContext(dispatcher) {
            localDataSource.obtenerAmanecer(tomorrowDate)
        }
    }

    private suspend fun obtenerAmanecerAPI(latitud: String, longitud: String): Amanecer {
        val tomorrowDate = LocalDate.now().plusDays(1)
        return withContext(dispatcher) {
            remoteDataSource.obtenerAmanecerAPI(tomorrowDate, latitud, longitud)
        }
    }

    /**
     * Inserta un amanecer en forma de [AmanecerNetwork] en la BD.
     * Elimina el último amanecer en caso de que existan más de 30.
     * @param amanecerAPI [Amanecer] de la API.
     */
    private suspend fun insertarAmanecer(amanecerAPI: Amanecer) {
        withContext(dispatcher) {
            localDataSource.insertarAmanecer(amanecerAPI)
        }
    }

    private fun obtenerAmanecerUsuario(origen: Origen): Amanecer {
        return preferencesDataSource.obtenerAmanecerUsuario(origen)
    }

    suspend fun obtenerUbicacion(): Location? {
        return withContext(dispatcher) {
            locationDataSource.obtenerUbicacion()
        }
    }

    /**
     * Obtiene el amanecer de mañana de la fuente local o de la remota, según se solicite.
     * Si no se puede conseguir el amanecer de ninguna de esas 2 fuentes, se obtiene de las preferencias del usuario.
     * @param ubicacion [Location] la ubicacion del dispositivo.
     * @param forzarActualizacion [Boolean] si es true, se tratarán de obtener los datos de internet aunque ya estén o no en la fuente local.
     */
    suspend fun obtenerAmanecerDiario(
        ubicacion: Location?,
        forzarActualizacion: Boolean
    ): Amanecer {
        return withContext(dispatcher) {
            if (!forzarActualizacion) {
                val amanecerBD = obtenerAmanecerBD()
                if (amanecerBD != null) {
                    return@withContext amanecerBD
                }
            }

            if (ubicacion != null) {
                try {
                    val amanecerAPI = obtenerAmanecerAPI(
                        ubicacion.latitude.toString(),
                        ubicacion.longitude.toString()
                    )
                    insertarAmanecer(amanecerAPI)
                    return@withContext amanecerAPI
                } catch (e: Exception) {
                    val amanecerBD = obtenerAmanecerBD()
                    if (amanecerBD != null) {
                        return@withContext amanecerBD
                    }

                    return@withContext obtenerAmanecerUsuario(Origen.USUARIO_NORED)
                }
            }

            return@withContext obtenerAmanecerUsuario(Origen.USUARIO_NOUBIC)
        }
    }

    fun establecerPreferenciasAlarma(alarma: Alarma): Alarma {
        return preferencesDataSource.establecerPreferenciasAlarma(alarma)
    }

    fun guardarEstadoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_on", alarma.encendida)

    /**
     * Guarda la configuración de vibración de la alarma (sí/no).
     */
    fun guardarVibrarAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_vib", alarma.vibrar)

    /**
     * Guarda la hora a la que sonará la alarma.
     */
    fun guardarHorasAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_hr", alarma.horasDiferencia)

    /**
     * Guarda los minutos a los que sonará la alarma.
     */
    fun guardarMinAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias(
            "${alarma.id}_min",
            alarma.minutosDiferencia
        )

    /**
     * Guarda el momento en el que sonará la alarma (antes/después del amanecer).
     */
    fun guardarMomentoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_momento", alarma.momento)

    fun guardarTonoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_tono", alarma.tono ?: "")

    fun guardarUriTonoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_uri", alarma.uriTono ?: "")
}