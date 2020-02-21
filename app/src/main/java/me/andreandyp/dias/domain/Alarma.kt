package me.andreandyp.dias.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

/**
 * Alarma que sirve para mostrar los datos guardados en las shared preferences. Todos los datos son observables.
 * @constructor Inicializa la alarma con los datos de las shared preferences.
 * @property [_id] ID para identificar la alarma (0..6).
 * @property [dia] Día de la semana.
 * @property [encendida] Estado de la alarma (on/off).
 * @property [vibrar] Para saber si la alarma vibra o no.
 * @property [horas] Hora a la que sonará la alarma.
 * @property [minutos] Minutos a los que sonará la alarma.
 * @property [tono] Tono que utilizará la alarma para sonar.
 * @property [horaFormateada] Hora de la alarma en el formato ±0:00.
 */
data class Alarma(
    val _id: Int,
    val dia: String,
    private var _encendida: Boolean,
    private var _vibrar: Boolean,
    private var _horas: Int,
    private var _minutos: Int,
    private var _momento: Int,
    private var _tono: String? = null
) : BaseObservable() {
    var encendida: Boolean
        @Bindable
        get() = _encendida
        set(value) {
            _encendida = value
            notifyPropertyChanged(BR.encendida)
        }
    var vibrar: Boolean
        @Bindable
        get() = _vibrar
        set(value) {
            _vibrar = value
            notifyPropertyChanged(BR.vibrar)
        }
    var horas: Int
        @Bindable
        get() = _horas
        set(value) {
            _horas = value
            notifyPropertyChanged(BR.horas)
            notifyPropertyChanged(BR.horaFormateada)
        }

    var minutos: Int
        @Bindable
        get() = _minutos
        set(value) {
            _minutos = value
            notifyPropertyChanged(BR.minutos)
            notifyPropertyChanged(BR.horaFormateada)
        }
    var momento: Int
        @Bindable
        get() = _momento
        set(value) {
            _momento = value
            notifyPropertyChanged(BR.momento)
            notifyPropertyChanged(BR.horaFormateada)
        }
    val horaFormateada: String
        @Bindable
        get() {
            val masMenos = when(momento){
                0 -> "-"
                1 -> "+"
                else -> "±"
            }
            return "${masMenos}${horas}:${if(minutos != 0) minutos.toString() else "00"}"
        }
}