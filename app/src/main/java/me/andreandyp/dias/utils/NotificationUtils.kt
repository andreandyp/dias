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

private const val POSPONER_CODE = -1

/**
 * Crear canal de notificaciones para Android 8.0 en adelante.
 * Luces y badge en el launcher activados.
 * Luces de color azul.
 * Se habilita la vibración pero se establece un patrón de 0 para que no vibre la notificación de forma predeterminada.
 */
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

/**
 * Enviar la notificación, al abrirla se muestra la activity con opciones.
 * Incluye una acción para posponer.
 */
fun enviarNotificacionAlarma(
    context: Context,
    notificationId: Int,
    horaFormateada: String
) {
    val preferencias = context.getSharedPreferences(context.getString(R.string.preference_file), Context.MODE_PRIVATE)

    val mostrarAlarmaIntent = Intent(context, MostrarAlarmaActivity::class.java)
    mostrarAlarmaIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    mostrarAlarmaIntent.putExtra(context.getString(R.string.notif_id_intent), notificationId)
    val mostrarAlarmaPending = PendingIntent.getActivity(
        context, notificationId, mostrarAlarmaIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )
    val posponerAlarmaIntent = Intent(context.applicationContext, PosponerReceiver::class.java)
    posponerAlarmaIntent.putExtra(context.getString(R.string.notif_id_intent), notificationId)
    val posponerAlarmaPending = PendingIntent.getBroadcast(
        context.applicationContext, POSPONER_CODE, posponerAlarmaIntent, PendingIntent.FLAG_CANCEL_CURRENT
    )
    val notificacion =
        NotificationCompat.Builder(context, context.getString(R.string.channel_id)).run {
            setContentTitle(context.getString(R.string.buenos_dias))
            setContentText(horaFormateada)
            setAutoCancel(true)
            setSmallIcon(android.R.drawable.alert_dark_frame)
            setContentIntent(mostrarAlarmaPending)
            priority = NotificationCompat.PRIORITY_MAX
            addAction(android.R.drawable.alert_dark_frame, context.getString(R.string.posponer), posponerAlarmaPending)
            setFullScreenIntent(mostrarAlarmaPending, true)
            build()
        }

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificacion)
    }

}