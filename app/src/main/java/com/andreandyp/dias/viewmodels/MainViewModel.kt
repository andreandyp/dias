package com.andreandyp.dias.viewmodels

import android.app.PendingIntent
import android.location.Location
import android.net.Uri
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreandyp.dias.BR
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoField
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase,
    private val saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase,
    private val configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase,
    private val turnOnAlarmUseCase: TurnOnAlarmUseCase,
    private val turnOffAlarmUseCase: TurnOffAlarmUseCase,
) : ViewModel() {
    val alarms = mutableListOf<Alarm>()

    private val _nextAlarm = MutableLiveData<Alarm>()
    val nextAlarm: LiveData<Alarm> = _nextAlarm

    private val _dataOrigin = MutableLiveData<Origin>()
    val dataOrigin: LiveData<Origin> = _dataOrigin

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _alarmStatusUpdated = MutableLiveData<Event<Alarm>>()
    val alarmStatusUpdated: LiveData<Event<Alarm>> = _alarmStatusUpdated

    init {
        val nextDay = LocalDate.now().plusDays(1)[ChronoField.DAY_OF_WEEK]
        for (i: Int in 1..7) {
            val alarm = setupAlarm(i, nextDay == i)
            alarms.add(alarm)
        }
    }

    fun setupNextAlarm(isLocationEnabled: Boolean, forceUpdate: Boolean = false) {
        _isLoading.value = true

        viewModelScope.launch {
            if (isLocationEnabled) {
                val location = getLastLocationUseCase()
                getNextAlarm(location, forceUpdate)
            } else {
                getNextAlarm(null, forceUpdate)
            }

        }
    }

    fun onRingtoneSelected(alarmId: Int, uri: Uri?, ringtone: String) {
        alarms[alarmId].tone = ringtone
        alarms[alarmId].uriTone = uri.toString()
    }

    fun onAlarmOn(alarmInstant: Instant, alarmPendingIntent: PendingIntent) {
        turnOnAlarmUseCase(alarmInstant, alarmPendingIntent)
    }

    fun onAlarmOff(alarmPendingIntent: PendingIntent, snoozePendingIntent: PendingIntent) {
        turnOffAlarmUseCase(alarmPendingIntent, snoozePendingIntent)
    }

    private suspend fun getNextAlarm(location: Location?, forceUpdate: Boolean) {
        val sunrise = getTomorrowSunriseUseCase(location, forceUpdate)

        _dataOrigin.value = sunrise.origin
        val nextDay: Alarm = alarms.first { it.isNextAlarm }
        nextDay.utcRingingAt = sunrise.dateTimeUTC
        _nextAlarm.value = nextDay
        _isLoading.value = false
    }

    private fun changeAlarmStatus(alarm: Alarm) {
        if (alarm.ringingAt != null) {
            _alarmStatusUpdated.value = Event(alarm)
        }
    }

    private fun setupAlarm(idAlarm: Int, isNextAlarm: Boolean): Alarm {
        val alarm = configureAlarmSettingsUseCase(idAlarm, isNextAlarm)
        alarm.day = DayOfWeek.of(alarm.id)

        alarm.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                sender as Alarm
                val field = when (propertyId) {
                    BR.on, BR.ringingAt -> {
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
