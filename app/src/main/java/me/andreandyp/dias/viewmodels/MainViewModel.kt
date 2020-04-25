package me.andreandyp.dias.viewmodels

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import me.andreandyp.dias.R
import me.andreandyp.dias.bd.DiasRepository
import me.andreandyp.dias.domain.Alarma
import me.andreandyp.dias.receivers.AlarmaReceiver
import me.andreandyp.dias.utils.AlarmUtils
import org.threeten.bp.ZoneId

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

    private val repository = DiasRepository(app.applicationContext)

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
        }?.addOnFailureListener {
            viewModelScope.launch {
                obtenerSiguienteAlarma(null)
            }
        }
    }

    private suspend fun obtenerSiguienteAlarma(ubicacion: Location?) {
        val amanecer = repository.obtenerAmanecerDiario(ubicacion)

        _datosDeInternet.value = amanecer.deInternet
        val siguienteDia = alarmas[amanecer.diaSemana - 1]
        siguienteDia.fechaHoraAmanecer = amanecer.fechaHoraLocal.toLocalDateTime()
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
    fun cambiarHorasAlarma(alarma: Alarma) =
        guardarPreferencias("${alarma._id}_hr", alarma.horasDiferencia)

    /**
     * Guarda los minutos a los que sonará la alarma.
     */
    fun cambiarMinAlarma(alarma: Alarma) =
        guardarPreferencias("${alarma._id}_min", alarma.minutosDiferencia)

    /**
     * Guarda el momento en el que sonará la alarma (antes/después del amanecer).
     */
    fun cambiarMomentoAlarma(alarma: Alarma) =
        guardarPreferencias("${alarma._id}_momento", alarma.momento)

    /**
     * Establecer la alarma con la hora de la alarma.
     * Si estamos manipulando la alarma de mañana, se encenderá o apagará la alarma siguiente,
     * si no, simplemente no se hará nada (el work manager se encargará de activarla).
     */
    private fun establecerAlarma(alarma: Alarma) {
        alarma.fechaHoraAmanecer?.let {
            // Intent para mostrar la alarma
            val mostrarAlarmaIntent = Intent(app.applicationContext, AlarmaReceiver::class.java)
            mostrarAlarmaIntent.putExtra(app.getString(R.string.notif_id_intent), alarma._id)
            val mostrarAlarmaPending = PendingIntent.getBroadcast(
                app.applicationContext,
                alarma._id,
                mostrarAlarmaIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            if (alarma.encendida) {
                var horaAlarmaUTC = alarma.fechaHoraSonar!!.atZone(ZoneId.systemDefault())
                //horaAlarmaUTC = horaAlarmaUTC.withHour(18).withMinute(57).withDayOfMonth(24)
                //Log.i("PRUEBA", horaAlarmaUTC.toString())
                AlarmUtils.encenderAlarma(app, horaAlarmaUTC.toInstant(), mostrarAlarmaPending)
            } else {
                AlarmUtils.apagarAlarma(app, mostrarAlarmaPending)
            }
        }
    }


}
