package me.andreandyp.dias.domain

data class Amanecer(
    val dia: Int,
    val mes: Int,
    val año: Int,
    var horas: Int,
    var minutos: Int,
    var segundos: Int,
    val deInternet: Boolean
)