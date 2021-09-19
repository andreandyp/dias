package com.andreandyp.dias.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import java.time.Instant
import javax.inject.Inject

class TurnOnAlarmUseCase @Inject constructor(
    private val alarmManager: AlarmManager,
) {
    operator fun invoke(alarmInstant: Instant, alarmPendingIntent: PendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmInstant.toEpochMilli(),
            alarmPendingIntent,
        )
    }
}