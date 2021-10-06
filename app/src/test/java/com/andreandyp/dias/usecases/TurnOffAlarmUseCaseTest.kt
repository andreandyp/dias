package com.andreandyp.dias.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreandyp.dias.DiasApplication
import com.andreandyp.dias.utils.Constants
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
    private lateinit var alarmPendingIntent: PendingIntent
    private lateinit var snoozePendingIntent: PendingIntent

    private lateinit var turnOffAlarmUseCase: TurnOffAlarmUseCase

    @Before
    fun setUp() {
        turnOffAlarmUseCase = TurnOffAlarmUseCase(alarmManager)

        alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.SNOOZE_ALARM_CODE,
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @Test
    fun `turns off pending alarm and snooze`() {
        turnOffAlarmUseCase(alarmPendingIntent, snoozePendingIntent)
        verify(alarmManager).cancel(alarmPendingIntent)
        verify(alarmManager).cancel(snoozePendingIntent)
    }
}