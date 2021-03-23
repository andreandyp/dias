package com.andreandyp.dias.preferences

import android.content.SharedPreferences
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.domain.Amanecer
import com.andreandyp.dias.domain.Origen
import com.andreandyp.dias.repository.PreferencesDataSource
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

class SharedPreferencesDataSource(private val preferencias: SharedPreferences) :
    PreferencesDataSource {
    override fun guardarPreferencias(clave: String, valor: Any) {
        with(preferencias.edit()) {
            when (valor) {
                is String -> putString(clave, valor)
                is Int -> putInt(clave, valor)
                is Boolean -> putBoolean(clave, valor)
                else -> putString(clave, valor.toString())
            }
            commit()
        }
    }

    override fun establecerPreferenciasAlarma(alarma: Alarma): Alarma {
        val (id) = alarma
        return alarma.apply {
            encendida = preferencias.getBoolean("${id}_on", false)
            vibrar = preferencias.getBoolean("${id}_vib", false)
            horasDiferencia = preferencias.getInt("${id}_hr", 0)
            minutosDiferencia = preferencias.getInt("${id}_min", 0)
            momento = preferencias.getInt("${id}_momento", -1)
            tono = preferencias.getString("${id}_tono", null)
            uriTono = preferencias.getString("${id}_uri", null)
        }
    }

    override fun obtenerAmanecerUsuario(origen: Origen): Amanecer {
        val horaPref = LocalTime.parse(preferencias.getString("hora_default", "07:00"))
        val tomorrowDate = ZonedDateTime.now(ZoneId.systemDefault())
            .plusDays(1)
            .withHour(horaPref[ChronoField.HOUR_OF_DAY])
            .withMinute(horaPref[ChronoField.MINUTE_OF_HOUR])
            .withSecond(0)
            .withNano(0)
        return Amanecer(
            diaSemana = tomorrowDate[ChronoField.DAY_OF_WEEK],
            fechaHoraLocal = tomorrowDate,
            origen = origen
        )
    }
}