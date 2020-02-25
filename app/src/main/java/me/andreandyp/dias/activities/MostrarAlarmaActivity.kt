package me.andreandyp.dias.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import me.andreandyp.dias.R
import me.andreandyp.dias.databinding.ActivityMostrarAlarmaBinding
import me.andreandyp.dias.receivers.AlarmaReceiver
import me.andreandyp.dias.receivers.PosponerReceiver
import org.threeten.bp.Instant
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoUnit

class MostrarAlarmaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMostrarAlarmaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMostrarAlarmaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val horaActual = LocalTime.now()
        val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        binding.horaActual.text = getString(R.string.hora_actual, formatter.format(horaActual))

        binding.posponerAlarma.setOnClickListener {
            val intent = Intent(this, PosponerReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                -1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val fecha = Instant.now()
                .plus(30, ChronoUnit.SECONDS)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fecha.toEpochMilli(),
                pendingIntent
            )

            this.finish()
        }
    }
}
