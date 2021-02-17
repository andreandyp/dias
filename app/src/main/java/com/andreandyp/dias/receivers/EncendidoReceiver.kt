package com.andreandyp.dias.receivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andreandyp.dias.R

class EncendidoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val preferencias = context.getSharedPreferences(
            context.getString(R.string.preference_file), Context.MODE_PRIVATE
        )

        for (i: Int in 0..6) {
            val encendida = preferencias.getBoolean("${i}_on", false)
            if (encendida) {
                /*var horaAlarmaUTC = alarma.fechaHoraSonar!!.atZone(ZoneId.systemDefault())
                encenderAlarma(context, i)*/
            }
        }
    }

    private fun encenderAlarma(context: Context, idAlarma: Int) {
        val appContext = context.applicationContext
        val mostrarAlarmaIntent = Intent(appContext, AlarmaReceiver::class.java)
        mostrarAlarmaIntent.putExtra(context.getString(R.string.notif_id_intent), idAlarma)

        val mostrarAlarmaPending = PendingIntent.getBroadcast(
            appContext, idAlarma, mostrarAlarmaIntent, PendingIntent.FLAG_CANCEL_CURRENT
        )
        //AlarmUtils.encenderAlarma(context, null, mostrarAlarmaPending)
    }
}