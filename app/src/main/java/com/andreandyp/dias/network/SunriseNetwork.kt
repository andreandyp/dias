package com.andreandyp.dias.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SunriseNetwork(
    val results: ResultsNetwork,
    val status: String
)
