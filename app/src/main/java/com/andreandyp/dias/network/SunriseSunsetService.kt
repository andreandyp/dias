package com.andreandyp.dias.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_URL = "https://api.sunrise-sunset.org"
private val moshi: Moshi = Moshi.Builder().run {
    add(KotlinJsonAdapterFactory())
    build()
}
private val retrofit: Retrofit = Retrofit.Builder().run {
    addConverterFactory(MoshiConverterFactory.create(moshi))
    baseUrl(API_URL)
    build()
}

interface SunriseSunsetService {
    @GET("/json?formatted=0&date=today")
    suspend fun fetchSunrise(
        @Query("date") date: String,
        @Query("lat") latitude: String,
        @Query("lng") longitude: String,
    ): SunriseNetwork
}

object SunriseSunsetAPI {
    val sunriseSunsetService: SunriseSunsetService by lazy {
        retrofit.create(SunriseSunsetService::class.java)
    }
}