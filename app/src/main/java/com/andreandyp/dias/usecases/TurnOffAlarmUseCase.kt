package com.andreandyp.dias.usecases

import android.app.AlarmManager
import android.app.PendingIntent
import javax.inject.Inject

class TurnOffAlarmUseCase @Inject constructor(
    private val alarmManager: AlarmManager,
) {
    operator fun invoke(alarmPendingIntent: PendingIntent, snoozePendingIntent: PendingIntent) {
        alarmManager.cancel(alarmPendingIntent)
        alarmManager.cancel(snoozePendingIntent)
    }
}