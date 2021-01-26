package com.andreandyp.dias.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andreandyp.dias.R
import com.andreandyp.dias.utils.NotificationUtils
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

/**
 * Receiver para cuando suena la alarma.
 */
class AlarmaReceiver : BroadcastReceiver() {
    /**
     * Aquí se recibe el aviso de que ya es hora de despertarse y se envía la notificación.
     * El ID de la notificación es el ID del día.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val horaActual = LocalTime.now()
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        val horaFormateada = context!!.getString(R.string.hora_actual, formatter.format(horaActual))
        val notifyId = intent?.extras?.getInt(context.getString(R.string.notif_id_intent)) ?: -1
        NotificationUtils.enviarNotificacionAlarma(context, notifyId, horaFormateada)
    }


}