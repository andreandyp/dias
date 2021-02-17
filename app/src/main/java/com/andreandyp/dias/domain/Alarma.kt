package com.andreandyp.dias.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import org.threeten.bp.LocalDateTime

/**
 * Alarma que sirve para mostrar los datos guardados en las shared preferences. Todos los datos son observables.
 * @constructor Inicializa la alarma con los datos esenciales.
 * @param [id] ID para identificar la alarma (0..6).
 * @param [esSiguienteAlarma] Es la siguiente alarma que sonará o no.
 *
 * @property [dia] Día de la semana en texto (Lunes, Martes, Miércoles...).
 * @property [encendida] Estado de la alarma (on/off).
 * @property [vibrar] La alarma vibra o no al sonar.
 * @property [horasDiferencia] Hora de retraso o adelanto con respecto al amanecer.
 * @property [minutosDiferencia] Minutos de retraso o adelanto con respecto al amanecer.
 * @property [momento] Sonará antes (0) o después (1) del amanecer.
 * @property [tono] Nombre del tono seleccionado.
 * @property [uriTono] URI del tono seleccionado.
 * @property [fechaHoraAmanecer] Fecha y hora a la que será el amanecer.
 *
 * @property [diferenciaFormateada] Retraso o adelanto seleccionado para la alarma en el formato ±0:00.
 * @property [fechaHoraSonar] Fecha y hora a la que sonará la alarma con retraso o adelanto incluido.
 * @property [horaFormateada] [fechaHoraSonar] en texto.
 */
data class Alarma(
    val id: Int,
    val esSiguienteAlarma: Boolean,
) : BaseObservable() {
    @get:Bindable
    var dia: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.dia)
        }

    @get:Bindable
    var encendida: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.encendida)
        }

    @get:Bindable
    var vibrar: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.vibrar)
        }

    @get:Bindable
    var horasDiferencia: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.horasDiferencia)
            notifyPropertyChanged(BR.diferenciaFormateada)
            notifyPropertyChanged(BR.fechaHoraSonar)
        }

    @get:Bindable
    var minutosDiferencia: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.minutosDiferencia)
            notifyPropertyChanged(BR.diferenciaFormateada)
            notifyPropertyChanged(BR.fechaHoraSonar)
        }

    @get:Bindable
    var momento: Int = -1
        set(value) {
            field = value
            notifyPropertyChanged(BR.momento)
            notifyPropertyChanged(BR.diferenciaFormateada)
            notifyPropertyChanged(BR.fechaHoraSonar)
        }

    @get:Bindable
    var tono: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.tono)
        }

    @get:Bindable
    var uriTono: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.uriTono)
        }

    var fechaHoraAmanecer: LocalDateTime? = null
        @Bindable
        set(value) {
            field = value
            notifyPropertyChanged(BR.fechaHoraAmanecer)
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
            return if (momento == 0) {
                fechaHoraAmanecer?.minusHours(horasDiferencia.toLong())
                    ?.minusMinutes(minutosDiferencia.toLong())
            } else {
                fechaHoraAmanecer?.plusHours(horasDiferencia.toLong())
                    ?.plusMinutes(minutosDiferencia.toLong())
            }
        }
    val horaFormateada: String
        get() = (fechaHoraSonar?.toLocalTime() ?: "N/A").toString()
}