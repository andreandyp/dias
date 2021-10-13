package com.andreandyp.dias.mocks

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.andreandyp.dias.utils.Constants

object ContextMocks {
    fun getAlarmPendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(),
        PendingIntent.FLAG_CANCEL_CURRENT
    )

    fun getSnoozePendingIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
        context,
        Constants.SNOOZE_ALARM_CODE,
        Intent(),
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}