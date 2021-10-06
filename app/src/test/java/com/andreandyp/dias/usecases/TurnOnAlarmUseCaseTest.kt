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
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class TurnOnAlarmUseCaseTest {
    private val context = ApplicationProvider.getApplicationContext<DiasApplication>()
    private val alarmManager: AlarmManager = spy(
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    )
    private val alarmPendingIntent = ContextMocks.getAlarmPendingIntent(context)

    private lateinit var turnOnAlarmUseCase: TurnOnAlarmUseCase

    @Before
    fun setUp() {
        turnOnAlarmUseCase = TurnOnAlarmUseCase(alarmManager)
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