package me.andreandyp.dias.network

import com.squareup.moshi.JsonClass
import me.andreandyp.dias.bd.entities.AmanecerEntity
import org.threeten.bp.OffsetDateTime

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
}

