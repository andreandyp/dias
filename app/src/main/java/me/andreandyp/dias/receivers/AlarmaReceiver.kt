package me.andreandyp.dias.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import me.andreandyp.dias.R
import me.andreandyp.dias.utils.enviarAlarma
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class AlarmaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val horaActual = LocalTime.now()
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        val horaFormateada = context!!.getString(R.string.hora_actual, formatter.format(horaActual))
        enviarAlarma(context, 0, horaFormateada)
    }


}