package com.andreandyp.dias.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.andreandyp.dias.R
import com.andreandyp.dias.receivers.AlarmaReceiver
import com.andreandyp.dias.receivers.PosponerReceiver
import org.threeten.bp.Instant

object AlarmUtils {
    private const val POSPONER_CODE = -1

    fun encenderAlarma(
        context: Context,
        horaAlarmaInstant: Instant,
        mostrarAlarmaPending: PendingIntent
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            horaAlarmaInstant.toEpochMilli(),
            mostrarAlarmaPending
        )
    }

    fun apagarAlarma(context: Context, mostrarAlarmaPending: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(mostrarAlarmaPending)

        // Intent para posponer la alarma (necesaria para cancelarla)
        val posponerIntent = Intent(context, PosponerReceiver::class.java)
        val posponerPending = PendingIntent.getBroadcast(
            context,
            POSPONER_CODE,
            posponerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(posponerPending)
    }

    fun crearIntentAlarma(context: Context, idAlarma: Int): PendingIntent {
        val mostrarAlarmaIntent = Intent(context, AlarmaReceiver::class.java)
        mostrarAlarmaIntent.putExtra(context.getString(R.string.notif_id_intent), idAlarma)
        return PendingIntent.getBroadcast(
            context,
            idAlarma,
            mostrarAlarmaIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
}