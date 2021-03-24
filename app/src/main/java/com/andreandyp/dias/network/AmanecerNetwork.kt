package com.andreandyp.dias.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AmanecerNetwork(
    val results: ResultsNetwork,
    val status: String
)
