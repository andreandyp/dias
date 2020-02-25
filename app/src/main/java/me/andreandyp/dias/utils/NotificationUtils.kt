package me.andreandyp.dias.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.andreandyp.dias.R
import me.andreandyp.dias.activities.MostrarAlarmaActivity
import me.andreandyp.dias.receivers.PosponerReceiver

fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nombre = context.getString(R.string.channel_nombre)
        val descripcion = context.getString(R.string.channel_descripcion)
        val importancia = NotificationManager.IMPORTANCE_HIGH
        val canal =
            NotificationChannel(context.getString(R.string.channel_id), nombre, importancia).apply {
                description = descripcion
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                setShowBadge(true)
            }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(canal)
    }
}

fun enviarAlarma(
    context: Context,
    notificationId: Int,
    horaFormateada: String
) {
    val mostrarAlarma = Intent(context, MostrarAlarmaActivity::class.java)
    val mostrarAlarmaPendingIntent = PendingIntent.getActivity(
        context, 0, mostrarAlarma, PendingIntent.FLAG_UPDATE_CURRENT
    )
    val posponerAlarma = Intent(context, PosponerReceiver::class.java)
    val posponerAlarmaPendingIntent = PendingIntent.getActivity(
        context, -1, posponerAlarma, PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notificacion =
        NotificationCompat.Builder(context, context.getString(R.string.channel_id)).run {
            setContentTitle(context.getString(R.string.buenos_dias))
            setContentText(horaFormateada)
            setAutoCancel(true)
            setSmallIcon(android.R.drawable.alert_dark_frame)
            setContentIntent(mostrarAlarmaPendingIntent)
            priority = NotificationCompat.PRIORITY_MAX
            addAction(android.R.drawable.alert_dark_frame, context.getString(R.string.posponer), posponerAlarmaPendingIntent)

            build()
        }

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificacion)
    }

}