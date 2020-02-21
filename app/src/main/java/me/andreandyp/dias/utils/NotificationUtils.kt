package me.andreandyp.dias.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import me.andreandyp.dias.R

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

fun enviarAlarma(context: Context, notificationId: Int) {
    val notificacion =
        NotificationCompat.Builder(context, context.getString(R.string.channel_id)).run {
            setContentTitle("Hola")
            setContentText("Probando...")
            setAutoCancel(true)
            setSmallIcon(android.R.drawable.alert_dark_frame)
            priority = NotificationCompat.PRIORITY_MAX

            build()
        }

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificacion)
    }

}