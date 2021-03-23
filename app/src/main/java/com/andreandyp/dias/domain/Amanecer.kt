package com.andreandyp.dias.domain

import org.threeten.bp.ZonedDateTime

data class Amanecer(
    val diaSemana: Int,
    val fechaHoraLocal: ZonedDateTime,
    val origen: Origen
)