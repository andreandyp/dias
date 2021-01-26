package com.andreandyp.dias.network

import com.squareup.moshi.JsonClass
import com.andreandyp.dias.bd.entities.AmanecerEntity
import com.andreandyp.dias.domain.Amanecer
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoField

@JsonClass(generateAdapter = true)
data class AmanecerNetwork(
    val results: ResultsNetwork,
    val status: String
) {
    /**
     * Convierte los datos del amanecer que vienen de internet a una entidad de la BD.
     * Debe convertir la fecha a un [OffsetDateTime] porque viene en formato UTC de la API.
     * Posteriormente se guarda de forma individual la fecha y la hora.
     * @return el amanecer en forma de [AmanecerEntity]
     */
    fun asEntity(): AmanecerEntity {
        val fechaHoraAmanecer = OffsetDateTime.parse(results.sunrise)

        return AmanecerEntity(
            _id = 0,
            amanecerFecha = fechaHoraAmanecer.toLocalDate(),
            amanecerHora = fechaHoraAmanecer.toLocalTime()
        )
    }

    /**
     * Convierte los datos del amanecer de la API a datos que se manejan en la app.
     * A la hora dela API se le específica el Offset (UTC) y posteriormente se convierte a la hora
     * en la zona horaria del dispositivo.
     * @return el amanecer en forma de [Amanecer] (Dominio)
     */
    fun asDomain(origen: Amanecer.Origen): Amanecer {
        val fechaHoraAPI = OffsetDateTime.parse(results.sunrise)
        val fechaHoraLocal = fechaHoraAPI.atZoneSameInstant(ZoneId.systemDefault())
        return Amanecer(
            diaSemana = fechaHoraLocal[ChronoField.DAY_OF_WEEK],
            fechaHoraLocal = fechaHoraLocal.withSecond(0),
            origen = origen
        )
    }
}

