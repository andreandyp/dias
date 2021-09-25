package com.andreandyp.dias.network

import retrofit2.http.GET
import retrofit2.http.Query

interface SunriseSunsetService {
    @GET("/json?formatted=0&date=today")
    suspend fun fetchSunrise(
        @Query("date") date: String,
        @Query("lat") latitude: String,
        @Query("lng") longitude: String,
    ): SunriseNetwork
}