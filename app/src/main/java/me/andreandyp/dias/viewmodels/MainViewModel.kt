package me.andreandyp.dias.viewmodels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.andreandyp.dias.R
import me.andreandyp.dias.bd.DiasRepository
import me.andreandyp.dias.domain.Alarma
import me.andreandyp.dias.receivers.AlarmaReceiver
import me.andreandyp.dias.receivers.PosponerReceiver
import org.threeten.bp.*
import org.threeten.bp.temporal.ChronoUnit

/**
 * ViewModel para la lista de alarmas.
 * @constructor Se llama desde [MainViewModelFactory].
 * @property [app] La aplicación para obtener los recursos.
 * @property [dias] Una [List] con los días de la semana.
 */
class MainViewModel(val app: Application, val dias: List<String>) : AndroidViewModel(app) {
    private val preferencias = app.getSharedPreferences(
        app.getString(R.string.preference_file), Context.MODE_PRIVATE
    )

    private val repository = DiasRepository(app)

    val alarmas = mutableListOf<Alarma>()

    private val _siguienteAlarma = MutableLiveData<Alarma>()
    val siguienteAlarma: LiveData<Alarma>
        get() = _siguienteAlarma

    private val _datosDeInternet = MutableLiveData<Boolean>()
    val datosDeInternet: LiveData<Boolean>
        get() = _datosDeInternet

    init {
        // Inicializar la lista de alarmas con los datos guardados en las shared preferences
        for (i: Int in 0..6) {
            alarmas.add(
                Alarma(
                    _id = i,
                    dia = dias[i],
                    _encendida = preferencias.getBoolean("${i}_on", false),
                    _vibrar = preferencias.getBoolean("${i}_vib", false),
                    _horasDiferencia = preferencias.getInt("${i}_hr", 0),
                    _minutosDiferencia = preferencias.getInt("${i}_min", 0),
                    _momento = preferencias.getInt("${i}_momento", -1)
                )
            )
        }
        obtenerUbicacion()
    }

    private fun obtenerUbicacion() {
        val fusedLocationClient: FusedLocationProviderClient? =
            LocationServices.getFusedLocationProviderClient(app.applicationContext)
        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            viewModelScope.launch {
                obtenerSiguienteAlarma(location)
            }
        }
    }

    private suspend fun obtenerSiguienteAlarma(ubicacion: Location?) {
        if (ubicacion == null) {
            Toast.makeText(
                app.applicationContext,
                "No se pudo obtener la ubicación más reciente",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val amanecer = withContext(Dispatchers.IO) {
            return@withContext repository.obtenerAmanecerDiario(
                ubicacion.latitude.toString(),
                ubicacion.longitude.toString()
            )
        }
        _datosDeInternet.value = amanecer.deInternet
        val siguienteDia = alarmas[amanecer.dia - 1]
        val horaAmanecer = LocalTime.of(amanecer.horas, amanecer.minutos)
        siguienteDia.horaAmanecer = horaAmanecer
        _siguienteAlarma.value = siguienteDia
    }

    /**
     * Guarda las preferencias según el tipo de dato que se le mande.
     * @param [clave] La clave de la preferencia a almacenar.
     * @param [valor] El valor de la preferencia a almacenar.
     */
    private fun guardarPreferencias(clave: String, valor: Any) {
        with(preferencias.edit()) {
            when (valor) {
                is String -> putString(clave, valor)
                is Int -> putInt(clave, valor)
                is Boolean -> putBoolean(clave, valor)
                else -> putString(clave, valor.toString())
            }
            commit()
        }
    }

    /**
     * Guarda el estado (on/off) de la alarma.
     */
    fun cambiarEstadoAlarma(alarma: Alarma) {
        establecerAlarma(alarma)
        guardarPreferencias("${alarma._id}_on", alarma.encendida)
    }

    /**
     * Guarda la configuración de vibración de la alarma (sí/no).
     */
    fun cambiarVibrarAlarma(alarma: Alarma) =
        guardarPreferencias("${alarma._id}_vib", alarma.vibrar)

    /**
     * Guarda la hora a la que sonará la alarma.
     */
    fun cambiarHorasAlarma(alarma: Alarma) {
        guardarPreferencias("${alarma._id}_hr", alarma.horasDiferencia)
        alarma.encendida = true
    }

    /**
     * Guarda los minutos a los que sonará la alarma.
     */
    fun cambiarMinAlarma(alarma: Alarma) {
        guardarPreferencias("${alarma._id}_min", alarma.minutosDiferencia)
        alarma.encendida = true
    }

    /**
     * Guarda el momento en el que sonará la alarma (antes/después del amanecer).
     */
    fun cambiarMomentoAlarma(alarma: Alarma) {
        guardarPreferencias("${alarma._id}_momento", alarma.momento)
        alarma.encendida = true
    }


    /**
     * Establecer la alarma con la hora de la alarma.
     * Si la alarma se activa, se establece la hora con ayuda del [AlarmManager].
     * Si la alarma se desactiva, se cancela la alarma en el [AlarmManager].
     */
    fun establecerAlarma(alarma: Alarma) {
        // Intent para mostrar la alarma
        val mostrarAlarmaIntent = Intent(app.applicationContext, AlarmaReceiver::class.java)
        mostrarAlarmaIntent.putExtra(app.getString(R.string.notif_id_intent), alarma._id)
        val mostrarAlarmaPending = PendingIntent.getBroadcast(
            app.applicationContext,
            alarma._id,
            mostrarAlarmaIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Encender la alarma o apagarla
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarma.encendida) {

            val s = LocalDate.now().plusDays(1).atTime(alarma.horaAmanecer)
            val hora = if (alarma.momento == 0) {
                s.minusHours(alarma.horasDiferencia.toLong()).minusMinutes(alarma.minutosDiferencia.toLong())
            }
            else{
                s.plusHours(alarma.horasDiferencia.toLong()).plusMinutes(alarma.minutosDiferencia.toLong())
            }

            val instante = hora.toInstant(ZoneOffset.UTC)
            Log.i("PRUEBA", "AQUI")
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                instante.toEpochMilli(),
                mostrarAlarmaPending
            )
        } else {
            alarmManager.cancel(mostrarAlarmaPending)

            // Intent para posponer la alarma (necesaria para cancelarla)
            val posponerIntent = Intent(app.applicationContext, PosponerReceiver::class.java)
            val posponerPending = PendingIntent.getBroadcast(
                app.applicationContext,
                POSPONER_CODE,
                posponerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(posponerPending)
        }
    }

    companion object {
        private const val POSPONER_CODE = -1
    }
}
