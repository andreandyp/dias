package com.andreandyp.dias.di

import com.andreandyp.dias.data.local.datasources.AlarmsLocalDataSource
import com.andreandyp.dias.data.local.datasources.AlarmsRoomDataSource
import com.andreandyp.dias.preferences.AlarmSharedPreferencesDataSource
import com.andreandyp.dias.repository.alarms.AlarmsPreferencesDataSource
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import com.andreandyp.dias.repository.alarms.AlarmsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Singleton
    @Binds
    abstract fun bindPreferenceDataSource(impl: AlarmSharedPreferencesDataSource): AlarmsPreferencesDataSource

    @Singleton
    @Binds
    abstract fun bindAlarmsRepository(impl: AlarmsRepositoryImpl): AlarmsRepository

    @Singleton
    @Binds
    abstract fun alarmsDataSource(impl: AlarmsRoomDataSource): AlarmsLocalDataSource
}
