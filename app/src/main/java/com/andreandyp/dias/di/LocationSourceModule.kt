package com.andreandyp.dias.di

import com.andreandyp.dias.location.GMSLocationDataSource
import com.andreandyp.dias.repository.location.LocationDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationSourceModule {
    @Singleton
    @Binds
    abstract fun bindLocationDataSource(impl: GMSLocationDataSource): LocationDataSource

}
