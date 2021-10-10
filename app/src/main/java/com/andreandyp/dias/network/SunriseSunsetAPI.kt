package com.andreandyp.dias.network

import androidx.annotation.VisibleForTesting
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.reflect.Modifier.PRIVATE

object SunriseSunsetAPI {
    private const val API_URL = "https://api.sunrise-sunset.org"
    private val moshi: Moshi = Moshi.Builder().run {
        add(KotlinJsonAdapterFactory())
        build()
    }

    @VisibleForTesting(otherwise = PRIVATE)
    fun getRetrofitInstance(apiUrl: String, okHttpClient: OkHttpClient? = null): Retrofit =
        Retrofit.Builder().run {
            if (okHttpClient != null) {
                client(okHttpClient)
            }
            addConverterFactory(MoshiConverterFactory.create(moshi))
            baseUrl(apiUrl)
            build()
        }

    val sunriseSunsetService: SunriseSunsetService by lazy {
        getRetrofitInstance(API_URL).create(SunriseSunsetService::class.java)
    }
}