package me.andreandyp.dias.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import me.andreandyp.dias.R
import me.andreandyp.dias.domain.Alarma

class MainViewModel(val app: Application, val dias: List<String>) : AndroidViewModel(app) {
    val preferencias = app.getSharedPreferences(
        app.getString(R.string.preference_file), Context.MODE_PRIVATE
    )

    val alarmas = mutableListOf<Alarma>()

    init {
        for (i: Int in 0..6) {
            alarmas.add(
                Alarma(
                    _id = i,
                    dia = dias[i],
                    encendida = preferencias.getBoolean("${i}_on", false),
                    vibrar = preferencias.getBoolean("${i}_vib", false),
                    horas = preferencias.getInt("${i}_hr", 0),
                    minutos = preferencias.getInt("${i}_min", 0),
                    momento = preferencias.getInt("${i}_min", 0)
                )
            )
        }
    }

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

    fun cambiarEstadoAlarma(dia: Int, encendida: Boolean) =
        guardarPreferencias("${dia}_on", encendida)

    fun cambiarVibrarAlarma(dia: Int, vibrar: Boolean) =
        guardarPreferencias("${dia}_vib", vibrar)

    fun cambiarHorasAlarma(dia: Int, horas: Int) =
        guardarPreferencias("${dia}_hr", horas)

    fun cambiarMinAlarma(dia: Int, minutos: Int) =
        guardarPreferencias("${dia}_min", minutos)

    fun cambiarMomentoAlarma(dia: Int, momento: Int) =
        guardarPreferencias("${dia}_momento", momento)
}
