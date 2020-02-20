package me.andreandyp.dias.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR

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
        }

    var minutos: Int
        @Bindable
        get() = _minutos
        set(value) {
            _minutos = value
            notifyPropertyChanged(BR.minutos)
        }
    var momento: Int
        @Bindable
        get() = _momento
        set(value) {
            _momento = value
            notifyPropertyChanged(BR.momento)
        }
}