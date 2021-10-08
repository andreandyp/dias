package com.andreandyp.dias.di

import com.andreandyp.dias.repository.alarms.AlarmsRepository
import com.andreandyp.dias.fakes.repository.alarms.FakeAlarmsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AlarmModule::class],
)
abstract class FakeAlarmModule {
    @Singleton
    @Binds
    abstract fun bindAlarmsRepository(fake: FakeAlarmsRepository): AlarmsRepository
}