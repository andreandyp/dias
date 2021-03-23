package com.andreandyp.dias.repository

import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.domain.Amanecer
import com.andreandyp.dias.domain.Origen

interface PreferencesDataSource {
    /**
     * Guarda las preferencias según el tipo de dato que se le mande.
     * @param [clave] La clave de la preferencia a almacenar.
     * @param [valor] El valor de la preferencia a almacenar.
     */
    fun guardarPreferencias(clave: String, valor: Any)

    /**
     * Obtiene las preferencias de la alarma con base en los ajustes del usuario.
     * @param [alarma] Alarma a la que se le establecerán los ajustes
     * @return La alarma con los ajustes establecidos.
     */
    fun establecerPreferenciasAlarma(alarma: Alarma): Alarma

    /**
     * Obtiene el amanecer de las preferencias del usuario.
     * @param [origen] el origen de la alarma. En este caso, puede ser [Origen.USUARIO_NORED] o [Origen.USUARIO_NORED]
     */
    fun obtenerAmanecerUsuario(origen: Origen): Amanecer
}