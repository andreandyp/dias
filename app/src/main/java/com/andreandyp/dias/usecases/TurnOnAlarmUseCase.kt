package com.andreandyp.dias.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import java.time.Instant
import javax.inject.Inject

open class TurnOnAlarmUseCase @Inject constructor(
    private val alarmManager: AlarmManager,
) {
    open operator fun invoke(alarmInstant: Instant, alarmPendingIntent: PendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmInstant.toEpochMilli(),
            alarmPendingIntent,
        )
    }
}