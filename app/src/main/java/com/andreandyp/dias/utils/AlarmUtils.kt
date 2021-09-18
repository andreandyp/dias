package com.andreandyp.dias.utils

import android.app.AlarmManager
import android.app.PendingIntent
import java.time.Instant

fun AlarmManager.turnOnAlarm(alarmInstant: Instant, alarmPendingIntent: PendingIntent) {
    setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        alarmInstant.toEpochMilli(),
        alarmPendingIntent,
    )
}

fun AlarmManager.turnOffAlarm(
    alarmPendingIntent: PendingIntent,
    snoozePendingIntent: PendingIntent
) {
    cancel(alarmPendingIntent)
    cancel(snoozePendingIntent)
}