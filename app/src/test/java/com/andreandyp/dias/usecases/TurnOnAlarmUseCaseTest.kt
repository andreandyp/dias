package com.andreandyp.dias.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreandyp.dias.DiasApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class TurnOnAlarmUseCaseTest {
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmPendingIntent: PendingIntent

    private val turnOnAlarmUseCase by lazy {
        TurnOnAlarmUseCase(alarmManager)
    }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<DiasApplication>()
        alarmManager = spy(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)

        alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    @Test
    fun `turns on alarm`() {
        val instant = Instant.now()
        turnOnAlarmUseCase(instant, alarmPendingIntent)
        verify(alarmManager).setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            instant.toEpochMilli(),
            alarmPendingIntent
        )
    }
}