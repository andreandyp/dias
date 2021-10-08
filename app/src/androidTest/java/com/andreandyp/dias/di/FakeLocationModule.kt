package com.andreandyp.dias.di

import com.andreandyp.dias.fakes.repository.location.FakeLocationRepository
import com.andreandyp.dias.repository.location.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LocationSourceModule::class],
)
abstract class FakeLocationModule {
    @Singleton
    @Binds
    abstract fun bindLocationRepository(fake: FakeLocationRepository): LocationRepository
}