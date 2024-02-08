package com.andreandyp.dias.di

import android.content.Context
import com.andreandyp.dias.bd.DiasDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): DiasDatabase {
        return DiasDatabase.getDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideDao(diasDatabase: DiasDatabase) = diasDatabase.sunriseDao()

    @Provides
    @Singleton
    fun provideAlarmsDao(diasDatabase: DiasDatabase) = diasDatabase.alarmsDao()
}
