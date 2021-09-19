package com.andreandyp.dias.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.andreandyp.dias.R
import com.andreandyp.dias.receivers.AlarmaReceiver
import com.andreandyp.dias.receivers.PosponerReceiver

object AlarmUtils {
    fun createAlarmPendingIntent(context: Context, alarmId: Int): PendingIntent {
        val alarmIntent = Intent(context, AlarmaReceiver::class.java)
        alarmIntent.putExtra(context.getString(R.string.notif_id_intent), alarmId)
        return PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
    fun createSnoozePendingIntent(context: Context): PendingIntent {
        val snoozeIntent = Intent(context, PosponerReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            Constants.SNOOZE_ALARM_CODE,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}