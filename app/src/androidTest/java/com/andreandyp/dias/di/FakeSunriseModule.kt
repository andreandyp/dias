package com.andreandyp.dias.di

import com.andreandyp.dias.repository.sunrise.FakeSunriseRepository
import com.andreandyp.dias.repository.sunrise.SunriseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SunriseModule::class],
)
abstract class FakeSunriseModule {
    @Singleton
    @Binds
    abstract fun bindRepository(fake: FakeSunriseRepository): SunriseRepository
}