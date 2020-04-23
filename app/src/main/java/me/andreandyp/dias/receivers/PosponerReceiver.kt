package me.andreandyp.dias.receivers

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import me.andreandyp.dias.R
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit

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
        val alarmaPending = PendingIntent.getBroadcast(
            context,
            POSPONER_CODE,
            alarmaIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Añadir los minutos que configuró el usuario para posponer
        val fecha = Instant.now()
            .plus(15, ChronoUnit.MINUTES)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            fecha.toEpochMilli(),
            alarmaPending
        )

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        // Cancelar la notificación de la alarma
        notificationManager.cancel(intent?.extras?.getInt(context.getString(R.string.notif_id_intent)) ?: -1)
    }

    companion object {
        private const val POSPONER_CODE = -1
    }
}