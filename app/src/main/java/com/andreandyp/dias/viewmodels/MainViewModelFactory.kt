package com.andreandyp.dias.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Clase para generar MainViewModel.
 * @constructor Recibe la aplicación actual y la lista de días, ambos utilizados por el ViewModel.
 * @property [app] La aplicación que utilizará el ViewModel.
 * @property [dias] La lista de días de la semana que utilizará el ViewModel.
 */
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(val app: Application, val dias: List<String>) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                app,
                dias
            ) as T // No sé cómo eliminar este unchecked cast sin la anotación @Suppress
        }

        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}