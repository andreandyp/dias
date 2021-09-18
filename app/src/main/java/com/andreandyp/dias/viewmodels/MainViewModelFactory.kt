package com.andreandyp.dias.viewmodels

import android.app.AlarmManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.SaveAlarmSettingsUseCase

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase,
    private val saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase,
    private val configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase,
    private val hasLocationPermission: Boolean,
    private val alarmManager: AlarmManager,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                getLastLocationUseCase,
                getTomorrowSunriseUseCase,
                saveAlarmSettingsUseCase,
                configureAlarmSettingsUseCase,
                hasLocationPermission,
                alarmManager,
            ) as T
        }

        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}