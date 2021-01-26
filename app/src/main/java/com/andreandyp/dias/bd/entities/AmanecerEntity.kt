package com.andreandyp.dias.bd.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreandyp.dias.domain.Amanecer
import org.threeten.bp.*
import org.threeten.bp.temporal.ChronoField

@Entity(tableName = "Tiempo")
data class AmanecerEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Long = 0,
    @ColumnInfo(name = "amanecerFecha")
    val amanecerFecha: LocalDate,
    @ColumnInfo(name = "amanecerHora")
    val amanecerHora: LocalTime
) {
    /**
     * Convierte los datos del amanecer almacenado en la BD a datos que se manejan en la app.
     * A la hora de la BD se le específica el Offset (UTC) y posteriormente se convierte a la hora
     * en la zona horaria del dispositivo.
     * Cuenta como "de internet" porque se almacenó en la BD desde la API.
     * @return el amanecer en forma de [Amanecer] (Dominio)
     */
    fun asDomain(origen: Amanecer.Origen): Amanecer {
        val fechaHoraBD = LocalDateTime.of(amanecerFecha, amanecerHora).atOffset(ZoneOffset.UTC)
        val fechaHoraLocal = fechaHoraBD.atZoneSameInstant(ZoneId.systemDefault())
        return Amanecer(
            diaSemana = fechaHoraLocal[ChronoField.DAY_OF_WEEK],
            fechaHoraLocal = fechaHoraLocal.withSecond(0),
            origen = origen
        )
    }
}
