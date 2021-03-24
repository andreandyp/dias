package com.andreandyp.dias.bd.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreandyp.dias.domain.Amanecer
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Entity(tableName = "Tiempo")
data class AmanecerEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Long = 0,
    @ColumnInfo(name = "amanecerFecha")
    val amanecerFecha: LocalDate,
    @ColumnInfo(name = "amanecerHora")
    val amanecerHora: LocalTime
)

/**
 * Convierte el amanecer de dominio a amanecer de base de datos (Room).
 * La fecha y hora se guardan de forma separada para poder buscar con base en la fecha.
 * Además la hora se guarda en la zona +00:00.
 * @return el amanecer en forma de [AmanecerEntity] (entidad de Room).
 */
fun Amanecer.asEntity(): AmanecerEntity {
    val amanecerFecha = fechaHoraUTC.toLocalDate()
    val amanecerHora = fechaHoraUTC.toLocalTime()
    return AmanecerEntity(
        amanecerFecha = amanecerFecha,
        amanecerHora = amanecerHora,
    )
}