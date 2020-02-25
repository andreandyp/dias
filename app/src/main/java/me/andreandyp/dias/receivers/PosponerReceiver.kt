package me.andreandyp.dias.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

class PosponerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(context, AlarmaReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            -1,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val fecha = Instant.now()
            .plus(2, ChronoUnit.MINUTES)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            fecha.toEpochMilli(),
            pendingIntent
        )
    }
}