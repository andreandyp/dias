package com.andreandyp.dias.viewmodels

import android.app.Application
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andreandyp.dias.BR
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.domain.Amanecer
import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.SaveAlarmSettingsUseCase
import com.andreandyp.dias.utils.AlarmUtils
import kotlinx.coroutines.launch
import org.threeten.bp.Instant
import org.threeten.bp.ZonedDateTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoField

/**
 * ViewModel para la lista de alarmas.
 * @constructor Se llama desde [MainViewModelFactory].
 * @property [app] La aplicación para obtener los recursos.
 * @property [dias] Una [List] con los días de la semana.
 */
class MainViewModel(
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase,
    private val saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase,
    private val configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase,
    private val tienePermisoDeUbicacion: Boolean,
    val app: Application,
    val dias: List<String>,
) : AndroidViewModel(app) {
    val alarmas = mutableListOf<Alarma>()
    val alarms = mutableListOf<Alarm>()

    private val _siguienteAlarma = MutableLiveData<Alarma>()
    val siguienteAlarma: LiveData<Alarma> = _siguienteAlarma

    private val _origenDatos = MutableLiveData<Origen>()
    val origenDatos: LiveData<Origen> = _origenDatos

    private val _actualizandoDatos = MutableLiveData(false)
    val actualizandoDatos: LiveData<Boolean> = _actualizandoDatos

    init {
        val nextDay = LocalDate.now().plusDays(1)[ChronoField.DAY_OF_WEEK]
        for (i: Int in dias.indices) {
            val alarm = setupAlarm(i + 1, nextDay == i + 1)
            alarms.add(alarm)
        }

        obtenerUbicacion(false)
    }

    fun obtenerUbicacion(forzarActualizacion: Boolean) {
        _actualizandoDatos.value = true

        if (!tienePermisoDeUbicacion) {
            viewModelScope.launch {
                obtenerSiguienteAlarma(null, forzarActualizacion)
            }
            return
        }

        viewModelScope.launch {
            val location = getLastLocationUseCase()
            obtenerSiguienteAlarma(location, forzarActualizacion)
        }
    }

    fun onRingtoneSeleccionado(alarmId: Int, uri: Uri?, ringtone: String) {
        alarms[alarmId].tone = ringtone
        alarms[alarmId].uriTone = uri.toString()
    }

    private suspend fun obtenerSiguienteAlarma(ubicacion: Location?, forzarActualizacion: Boolean) {
        val sunrise = getTomorrowSunriseUseCase(ubicacion, forzarActualizacion)
        val amanecer = Amanecer(
            sunrise.dayOfWeek.value,
            ZonedDateTime.parse(sunrise.dateTimeUTC.toString()),
            sunrise.origin,
        )
        Log.i("PRUEBA", amanecer.origen.toString())

        _origenDatos.value = amanecer.origen
        val siguienteDia = alarmas[amanecer.diaSemana - 1]
        siguienteDia.fechaHoraAmanecer = amanecer.fechaHoraUTC.toLocalDateTime()
        _siguienteAlarma.value = siguienteDia
        _actualizandoDatos.value = false
    }

    private fun changeAlarmStatus(alarm: Alarm) {
        alarm.ringingAt?.let {
            val pendingIntent = AlarmUtils.crearIntentAlarma(app.applicationContext, alarm.id)

            if (alarm.on) {
                val dateTimeUTC = alarm.ringingAt!!.atZone(ZoneId.systemDefault())

                AlarmUtils.encenderAlarma(
                    app.applicationContext,
                    Instant.parse(dateTimeUTC.toString()),
                    pendingIntent
                )
            } else {
                AlarmUtils.apagarAlarma(app.applicationContext, pendingIntent)
            }
        }
    }

    private fun setupAlarm(idAlarm: Int, isNextAlarm: Boolean): Alarm {
        val alarm = configureAlarmSettingsUseCase(idAlarm)
        alarm.day = DayOfWeek.of(alarm.id)
        alarm.isNextAlarm = isNextAlarm

        alarm.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                sender as Alarm
                val field = when (propertyId) {
                    BR.on -> {
                        changeAlarmStatus(alarm)
                        Alarm.Field.ON
                    }
                    BR.ringingAt -> {
                        changeAlarmStatus(alarm)
                        Alarm.Field.ON
                    }
                    BR.vibration -> Alarm.Field.VIBRATION
                    BR.tone -> Alarm.Field.TONE
                    BR.uriTone -> Alarm.Field.URI_TONE
                    BR.offsetHours -> Alarm.Field.OFFSET_HOURS
                    BR.offsetMinutes -> Alarm.Field.OFFSET_MINUTES
                    BR.offsetType -> Alarm.Field.OFFSET_TYPE
                    else -> Alarm.Field.ON
                }

                saveAlarmSettingsUseCase(alarm, field)
            }

        })

        return alarm
    }
}
