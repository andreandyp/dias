package me.andreandyp.dias.viewmodels

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import me.andreandyp.dias.R
import me.andreandyp.dias.domain.Alarma
import me.andreandyp.dias.receivers.AlarmaReceiver
import me.andreandyp.dias.receivers.PosponerReceiver
import org.threeten.bp.Instant
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

    val alarmas = mutableListOf<Alarma>()

    init {
        // Inicializar la lista de alarmas con los datos guardados en las shared preferences
        for (i: Int in 0..6) {
            alarmas.add(
                Alarma(
                    _id = i,
                    dia = dias[i],
                    _encendida = preferencias.getBoolean("${i}_on", false),
                    _vibrar = preferencias.getBoolean("${i}_vib", false),
                    _horas = preferencias.getInt("${i}_hr", 0),
                    _minutos = preferencias.getInt("${i}_min", 0),
                    _momento = preferencias.getInt("${i}_momento", -1)
                )
            )
        }
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
        guardarPreferencias("${alarma._id}_hr", alarma.horas)

    /**
     * Guarda los minutos a los que sonará la alarma.
     */
    fun cambiarMinAlarma(alarma: Alarma) =
        guardarPreferencias("${alarma._id}_min", alarma.minutos)

    /**
     * Guarda el momento en el que sonará la alarma (antes/después del amanecer).
     */
    fun cambiarMomentoAlarma(alarma: Alarma) =
        guardarPreferencias("${alarma._id}_momento", alarma.momento)

    /**
     * Establecer la alarma con la hora de la alarma.
     * Si la alarma se activa, se establece la hora con ayuda del [AlarmManager].
     * Si la alarma se desactiva, se cancela la alarma en el [AlarmManager].
     */
    fun establecerAlarma(alarma: Alarma) {
        // Intent para mostrar la alarma
        val mostrarAlarmaIntent = Intent(app.applicationContext, AlarmaReceiver::class.java)
        mostrarAlarmaIntent.putExtra("notify_id", alarma._id)
        val mostrarAlarmaPending = PendingIntent.getBroadcast(
            app.applicationContext,
            alarma._id,
            mostrarAlarmaIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Encender la alarma o apagarla
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarma.encendida) {
            val fecha = Instant.now()
                .plus(alarma.horas.toLong(), ChronoUnit.MINUTES)
                .plus(alarma.minutos.toLong(), ChronoUnit.SECONDS)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fecha.toEpochMilli(),
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
