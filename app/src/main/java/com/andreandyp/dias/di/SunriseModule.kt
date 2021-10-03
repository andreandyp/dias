package com.andreandyp.dias.di

import com.andreandyp.dias.bd.SunriseRoomDataSource
import com.andreandyp.dias.network.SunriseRetrofitDataSource
import com.andreandyp.dias.preferences.SunriseSharedPreferencesDataSource
import com.andreandyp.dias.repository.sunrise.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SunriseModule {
    @Singleton
    @Binds
    abstract fun bindPreferenceDataSource(impl: SunriseSharedPreferencesDataSource): SunrisePreferenceDataSource

    @Singleton
    @Binds
    abstract fun bindLocalDataSource(impl: SunriseRoomDataSource): SunriseLocalDataSource

    @Singleton
    @Binds
    abstract fun bindRemoteDataSource(impl: SunriseRetrofitDataSource): SunriseRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindRepository(impl: SunriseRepositoryImpl): SunriseRepository
}