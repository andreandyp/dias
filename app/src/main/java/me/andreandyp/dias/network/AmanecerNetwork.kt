package me.andreandyp.dias.network

import com.squareup.moshi.JsonClass
import me.andreandyp.dias.bd.entities.AmanecerEntity
import me.andreandyp.dias.domain.Amanecer
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoField

@JsonClass(generateAdapter = true)
data class AmanecerNetwork(
    val results: ResultsNetwork,
    val status: String
) {
    fun asEntity(): AmanecerEntity {
        return AmanecerEntity(
            _id = 0,
            amanecerFechaHora = OffsetDateTime.parse(results.sunrise).toInstant()
        )
    }

    fun asDomain(): Amanecer {
        val instante = OffsetDateTime.parse(results.sunrise)
        val local = instante.atZoneSameInstant(ZoneId.systemDefault())
        return Amanecer(
            dia = local[ChronoField.DAY_OF_WEEK],
            mes = local[ChronoField.MONTH_OF_YEAR],
            año = local[ChronoField.YEAR],
            horas = local[ChronoField.HOUR_OF_DAY],
            minutos = local[ChronoField.MINUTE_OF_HOUR],
            segundos = local[ChronoField.SECOND_OF_MINUTE],
            deInternet = true
        )
    }
}

