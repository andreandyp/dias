package com.andreandyp.dias.viewmodels

import android.app.Application
import android.location.Location
import android.net.Uri
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andreandyp.dias.BR
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.SaveAlarmSettingsUseCase
import com.andreandyp.dias.utils.AlarmUtils
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField

class MainViewModel(
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase,
    private val saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase,
    private val configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase,
    private val hasLocationPermission: Boolean,
    val app: Application,
    val dias: List<String>,
) : AndroidViewModel(app) {
    val alarms = mutableListOf<Alarm>()

    private val _nextAlarm = MutableLiveData<Alarm>()
    val nextAlarm: LiveData<Alarm> = _nextAlarm

    private val _dataOrigin = MutableLiveData<Origen>()
    val dataOrigin: LiveData<Origen> = _dataOrigin

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val nextDay = LocalDate.now().plusDays(1)[ChronoField.DAY_OF_WEEK]
        for (i: Int in dias.indices) {
            val alarm = setupAlarm(i + 1, nextDay == i + 1)
            alarms.add(alarm)
        }

        fetchLocation(false)
    }

    fun fetchLocation(forceUpdate: Boolean) {
        _isLoading.value = true

        if (!hasLocationPermission) {
            viewModelScope.launch {
                getNextAlarm(null, forceUpdate)
            }
            return
        }

        viewModelScope.launch {
            val location = getLastLocationUseCase()
            getNextAlarm(location, forceUpdate)
        }
    }

    fun onRingtoneSelected(alarmId: Int, uri: Uri?, ringtone: String) {
        alarms[alarmId].tone = ringtone
        alarms[alarmId].uriTone = uri.toString()
    }

    private suspend fun getNextAlarm(location: Location?, forceUpdate: Boolean) {
        val sunrise = getTomorrowSunriseUseCase(location, forceUpdate)

        _dataOrigin.value = sunrise.origin
        val nextDay = alarms[sunrise.dayOfWeek.minus(1).value]
        nextDay.utcRingingAt = sunrise.dateTimeUTC
        _nextAlarm.value = nextDay
        _isLoading.value = false
    }

    private fun changeAlarmStatus(alarm: Alarm) {
        alarm.ringingAt?.let {
            val pendingIntent = AlarmUtils.crearIntentAlarma(app.applicationContext, alarm.id)

            if (alarm.on) {
                AlarmUtils.encenderAlarma(
                    app.applicationContext,
                    it.toInstant(),
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
