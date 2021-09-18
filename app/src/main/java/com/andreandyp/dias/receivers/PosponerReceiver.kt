package com.andreandyp.dias.receivers

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.andreandyp.dias.R
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Receiver para posponer la alarma.
 */
class PosponerReceiver : BroadcastReceiver() {
    /**
     * Aquí se recibe la acción de posponer de la notificación y de la pantalla que abre la notificación.
     * Mediante la [intent] se recibe el ID de la notificación para cancelarla.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmaIntent = Intent(context, AlarmaReceiver::class.java)
        val notifId = intent?.extras?.getInt(context?.getString(R.string.notif_id_intent)) ?: -1
        alarmaIntent.putExtra(context?.getString(R.string.notif_id_intent), notifId)
        val alarmaPending = PendingIntent.getBroadcast(
            context,
            POSPONER_CODE,
            alarmaIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val preferencias = context.getSharedPreferences(
            context.getString(R.string.preference_file), Context.MODE_PRIVATE
        )

        // Añadir los minutos que configuró el usuario para posponer
        val fecha = Instant.now()
            .plus(preferencias.getString("posponer_minutos", "15")!!.toLong(), ChronoUnit.MINUTES)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            fecha.toEpochMilli(),
            alarmaPending
        )

        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).cancel()

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        // Cancelar la notificación de la alarma
        notificationManager.cancel(notifId)
    }

    companion object {
        private const val POSPONER_CODE = -1
    }
}