package com.andreandyp.dias.di

import android.app.AlarmManager
import android.content.Context
import android.content.SharedPreferences
import com.andreandyp.dias.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.getString(R.string.preference_file), Context.MODE_PRIVATE
        )
    }
}