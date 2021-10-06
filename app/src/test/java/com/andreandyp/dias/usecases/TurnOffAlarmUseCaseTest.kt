package com.andreandyp.dias.usecases

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreandyp.dias.DiasApplication
import com.andreandyp.dias.mocks.ContextMocks
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class TurnOffAlarmUseCaseTest {
    private val context = ApplicationProvider.getApplicationContext<DiasApplication>()
    private val alarmManager: AlarmManager = spy(
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    )
    private val alarmPendingIntent = ContextMocks.getAlarmPendingIntent(context)
    private val snoozePendingIntent = ContextMocks.getSnoozePendingIntent(context)

    private lateinit var turnOffAlarmUseCase: TurnOffAlarmUseCase

    @Before
    fun setUp() {
        turnOffAlarmUseCase = TurnOffAlarmUseCase(alarmManager)
    }

    @Test
    fun `turns off pending alarm and snooze`() {
        turnOffAlarmUseCase(alarmPendingIntent, snoozePendingIntent)
        verify(alarmManager).cancel(alarmPendingIntent)
        verify(alarmManager).cancel(snoozePendingIntent)
    }
}