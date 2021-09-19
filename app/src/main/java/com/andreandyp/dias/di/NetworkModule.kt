package com.andreandyp.dias.di

import com.andreandyp.dias.network.SunriseSunsetAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideSunriseSunsetService() = SunriseSunsetAPI.sunriseSunsetService
}