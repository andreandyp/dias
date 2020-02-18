package me.andreandyp.dias.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import me.andreandyp.dias.R

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val app = application
    val preferencias = app.getSharedPreferences(
        app.getString(R.string.preference_file), Context.MODE_PRIVATE
    )

    private fun guardarPreferencias(clave: String, valor: Any) {
        with(preferencias.edit()) {
            when (valor) {
                is String -> putString(clave, valor)
                is Int -> putInt(clave, valor)
                is Boolean -> putBoolean(clave, valor)
                else -> putString(clave, valor.toString())
            }
            apply()
        }
    }

    fun cambiarEstadoAlarma(dia: String, encendida: Boolean) =
        guardarPreferencias("${dia}_on", encendida)

    fun cambiarVibrarAlarma(dia: String, vibrar: Boolean) =
        guardarPreferencias("${dia}_vib", vibrar)

    fun cambiarHorasAlarma(dia: String, horas: Int) =
        guardarPreferencias("${dia}_hr", horas)

    fun cambiarMinAlarma(dia: String, minutos: Int) =
        guardarPreferencias("${dia}_min", minutos)

    fun cambiarMomentoAlarma(dia: String, momento: Int) =
        guardarPreferencias("${dia}_momento", momento)
}
