package me.andreandyp.dias.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import org.threeten.bp.LocalDateTime

/**
 * Alarma que sirve para mostrar los datos guardados en las shared preferences. Todos los datos son observables.
 * @constructor Inicializa la alarma con los datos de las shared preferences.
 * @property [_id] ID para identificar la alarma (0..6).
 * @property [dia] Día de la semana.
 * @property [encendida] Estado de la alarma (on/off).
 * @property [vibrar] Para saber si la alarma vibra o no.
 * @property [horasDiferencia] Hora de retraso o adelanto
 * @property [minutosDiferencia] Minutos de retraso o adelanto
 * @property [fechaHoraAmanecer] Fecha y hora a la que será el amanecer
 * @property [diferenciaFormateada] Retraso o adelanto seleccionado para la alarma en el formato ±0:00.
 * @property [fechaHoraSonar] Fecha y hora a la que sonará la alarma con retraso o adelanto incluido.
 * @property [horaFormateada] [fechaHoraSonar] en texto.
 */
data class Alarma(
    val _id: Int,
    val dia: String,
    val esSiguienteAlarma: Boolean,
    private var _encendida: Boolean,
    private var _vibrar: Boolean,
    private var _horasDiferencia: Int,
    private var _minutosDiferencia: Int,
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
    var horasDiferencia: Int
        @Bindable
        get() = _horasDiferencia
        set(value) {
            _horasDiferencia = value
            notifyPropertyChanged(BR.horasDiferencia)
            notifyPropertyChanged(BR.diferenciaFormateada)
            notifyPropertyChanged(BR.fechaHoraSonar)
        }
    var minutosDiferencia: Int
        @Bindable
        get() = _minutosDiferencia
        set(value) {
            _minutosDiferencia = value
            notifyPropertyChanged(BR.minutosDiferencia)
            notifyPropertyChanged(BR.diferenciaFormateada)
            notifyPropertyChanged(BR.fechaHoraSonar)
        }
    var fechaHoraAmanecer: LocalDateTime? = null
        @Bindable
        set(value) {
            field = value
            notifyPropertyChanged(BR.fechaHoraSonar)
        }
    var momento: Int
        @Bindable
        get() = _momento
        set(value) {
            _momento = value
            notifyPropertyChanged(BR.momento)
            notifyPropertyChanged(BR.diferenciaFormateada)
            notifyPropertyChanged(BR.fechaHoraSonar)
        }
    val diferenciaFormateada: String
        @Bindable
        get() {
            val masMenos = when (momento) {
                0 -> "-"
                1 -> "+"
                else -> "±"
            }
            return "${masMenos}${horasDiferencia}:${if (minutosDiferencia != 0) minutosDiferencia.toString() else "00"}"
        }
    val fechaHoraSonar: LocalDateTime?
        @Bindable
        get() {
            notifyPropertyChanged(BR.horaFormateada)
            return if (momento == 0) {
                fechaHoraAmanecer?.minusHours(horasDiferencia.toLong())
                    ?.minusMinutes(minutosDiferencia.toLong())
            } else {
                fechaHoraAmanecer?.plusHours(horasDiferencia.toLong())
                    ?.plusMinutes(minutosDiferencia.toLong())
            }
        }
    val horaFormateada: String
        @Bindable
        get() = (fechaHoraSonar?.toLocalTime() ?: "N/A").toString()
}