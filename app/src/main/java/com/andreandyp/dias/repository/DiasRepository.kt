package com.andreandyp.dias.repository

import com.andreandyp.dias.domain.Alarma

class DiasRepository(
    private val preferencesDataSource: PreferencesDataSource
) {
    fun establecerPreferenciasAlarma(alarma: Alarma): Alarma {
        return preferencesDataSource.establecerPreferenciasAlarma(alarma)
    }

    fun guardarEstadoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_on", alarma.encendida)

    /**
     * Guarda la configuración de vibración de la alarma (sí/no).
     */
    fun guardarVibrarAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_vib", alarma.vibrar)

    /**
     * Guarda la hora a la que sonará la alarma.
     */
    fun guardarHorasAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_hr", alarma.horasDiferencia)

    /**
     * Guarda los minutos a los que sonará la alarma.
     */
    fun guardarMinAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias(
            "${alarma.id}_min",
            alarma.minutosDiferencia
        )

    /**
     * Guarda el momento en el que sonará la alarma (antes/después del amanecer).
     */
    fun guardarMomentoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_momento", alarma.momento)

    fun guardarTonoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_tono", alarma.tono ?: "")

    fun guardarUriTonoAlarma(alarma: Alarma) =
        preferencesDataSource.guardarPreferencias("${alarma.id}_uri", alarma.uriTono ?: "")
}