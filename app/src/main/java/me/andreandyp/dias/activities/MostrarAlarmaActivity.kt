package me.andreandyp.dias.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.andreandyp.dias.R
import me.andreandyp.dias.databinding.ActivityMostrarAlarmaBinding
import me.andreandyp.dias.receivers.PosponerReceiver
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoUnit

/**
 * Mostrar la alarma y opciones para apagar o posponer
 */
class MostrarAlarmaActivity : AppCompatActivity() {
    private lateinit var bindingView: ActivityMostrarAlarmaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = ActivityMostrarAlarmaBinding.inflate(layoutInflater)
        setContentView(bindingView.root)

        val horaActual = LocalTime.now()
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        bindingView.horaActual.text = getString(R.string.hora_actual, formatter.format(horaActual))

        bindingView.posponerAlarma.setOnClickListener {
            val mostrarAlarmaIntent = Intent(this, PosponerReceiver::class.java)
            val mostrarAlarmaPending = PendingIntent.getBroadcast(
                this,
                -1,
                mostrarAlarmaIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val fecha = Instant.now()
                .plus(1, ChronoUnit.MINUTES)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fecha.toEpochMilli(),
                mostrarAlarmaPending
            )

            this.finish()
        }
    }
}
