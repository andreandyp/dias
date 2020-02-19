package me.andreandyp.dias.domain

data class Alarma(
    val _id: Int,
    val dia: String,
    var encendida: Boolean,
    var vibrar: Boolean,
    var horas: Int,
    var minutos: Int,
    var momento: Int,
    var tono: String? = null
)