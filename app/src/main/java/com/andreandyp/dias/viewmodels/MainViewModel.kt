package com.andreandyp.dias.viewmodels

import android.app.PendingIntent
import android.location.Location
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreandyp.dias.BR
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.ui.state.AlarmUiState
import com.andreandyp.dias.ui.state.MainState
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.SaveAlarmSettingsUseCase
import com.andreandyp.dias.usecases.TurnOffAlarmUseCase
import com.andreandyp.dias.usecases.TurnOnAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _nextAlarm = MutableLiveData<Alarm>()
    val nextAlarm: LiveData<Alarm> = _nextAlarm

    private val _alarmStatusUpdated = MutableLiveData<Event<Alarm>>()
    val alarmStatusUpdated: LiveData<Event<Alarm>> = _alarmStatusUpdated

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    val alarms = mutableStateListOf<AlarmUiState>()

    init {
        val nextDay = LocalDate.now().plusDays(1)[ChronoField.DAY_OF_WEEK]
        (1..7).map {
            setupAlarm(it, nextDay == it)
        }.map {
            AlarmUiState(
                it.id,
                it.formattedDay,
                it.formattedOffset,
                it.on,
                it.vibration,
                it.tone,
            )
        }.onEach(alarms::add)
    }

    fun setupNextAlarm(isLocationEnabled: Boolean, forceUpdate: Boolean = false) {
        _state.update {
            it.copy(loading = true)
        }

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
        /*alarms[alarmId].tone = ringtone
        alarms[alarmId].uriTone = uri.toString()*/
    }

    fun onAlarmOn(alarmInstant: Instant, alarmPendingIntent: PendingIntent) {
        turnOnAlarmUseCase(alarmInstant, alarmPendingIntent)
    }

    fun onAlarmOff(alarmPendingIntent: PendingIntent, snoozePendingIntent: PendingIntent) {
        turnOffAlarmUseCase(alarmPendingIntent, snoozePendingIntent)
    }

    fun onClickExpand(alarm: AlarmUiState) {
        val index = alarms.indexOfFirst { it.id == alarm.id }
        alarms[index] = alarm.copy(isConfigExpanded = alarm.isConfigExpanded.not())
    }

    private suspend fun getNextAlarm(location: Location?, forceUpdate: Boolean) {
        val sunrise = getTomorrowSunriseUseCase(location, forceUpdate)

        _state.update {
            it.copy(origin = sunrise.origin, loading = false)
        }
        // val nextDay: Alarm = alarms.first { it.isNextAlarm }
        // nextDay.utcRingingAt = sunrise.dateTimeUTC
        /*_nextAlarm.value = nextDay*/
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
