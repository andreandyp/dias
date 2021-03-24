package com.andreandyp.dias.domain

import com.andreandyp.dias.bd.entities.AmanecerEntity
import com.andreandyp.dias.network.AmanecerNetwork
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

data class Amanecer(
    val diaSemana: Int,
    val fechaHoraUTC: ZonedDateTime,
    val origen: Origen
)

/**
 * Convierte los datos del amanecer de la API a datos de dominio.
 * Los datos se conservan con la zona +00:00.
 * @return el amanecer en forma de [Amanecer] (dominio).
 */
fun AmanecerNetwork.asDomain(): Amanecer {
    val fechaHoraOffset = OffsetDateTime.parse(results.sunrise)
    val fechaHoraUTC = fechaHoraOffset.toZonedDateTime()
    return Amanecer(
        diaSemana = fechaHoraUTC[ChronoField.DAY_OF_WEEK],
        fechaHoraUTC = fechaHoraUTC.withSecond(0),
        origen = Origen.INTERNET,
    )
}

/**
 * Convierte los datos de amanecer de base de datos (Room) a amanecer de dominio.
 * Los datos se conservan con la zona +00:00.
 * @return el amanecer en forma de [Amanecer] (dominio).
 */
fun AmanecerEntity.asDomain(): Amanecer {
    val fechaHoraUTC = ZonedDateTime.of(
        amanecerFecha,
        amanecerHora,
        ZoneOffset.ofOffset("", ZoneOffset.UTC)
    )

    return Amanecer(
        diaSemana = fechaHoraUTC[ChronoField.DAY_OF_WEEK],
        fechaHoraUTC = fechaHoraUTC,
        origen = Origen.BD
    )
}