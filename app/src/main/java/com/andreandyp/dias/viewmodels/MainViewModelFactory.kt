package com.andreandyp.dias.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.SaveAlarmSettingsUseCase

/**
 * Clase para generar MainViewModel.
 * @constructor Recibe la aplicación actual y la lista de días, ambos utilizados por el ViewModel.
 * @property [app] La aplicación que utilizará el ViewModel.
 * @property [dias] La lista de días de la semana que utilizará el ViewModel.
 */
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase,
    private val saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase,
    private val configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase,
    private val tienePermisoDeUbicacion: Boolean,
    val app: Application,
    val dias: List<String>,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                getLastLocationUseCase,
                getTomorrowSunriseUseCase,
                saveAlarmSettingsUseCase,
                configureAlarmSettingsUseCase,
                tienePermisoDeUbicacion,
                app,
                dias,
            ) as T
        }

        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}