package me.andreandyp.dias.activities

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import me.andreandyp.dias.R
import me.andreandyp.dias.databinding.ActivityMostrarAlarmaBinding
import me.andreandyp.dias.receivers.PosponerReceiver
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

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

        val preferencias = this.getSharedPreferences(
            this.getString(R.string.preference_file),
            Context.MODE_PRIVATE
        )

        val notifID = intent?.extras?.getInt(this.getString(R.string.notif_id_intent)) ?: -1
        val hacerVibrar = preferencias.getBoolean("${notifID}_vib", false)
        val vibrador = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (hacerVibrar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrador.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 750, 500, 750, 500),
                        0
                    )
                )
            } else {
                vibrador.vibrate(longArrayOf(0, 750, 500, 750, 500), 0)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        bindingView.posponerAlarma.setOnClickListener {
            val posponerAlarmaIntent = Intent(this, PosponerReceiver::class.java)
            val notifId = intent?.extras?.getInt(this.getString(R.string.notif_id_intent)) ?: -1
            posponerAlarmaIntent.putExtra(getString(R.string.notif_id_intent), notifId)

            vibrador.cancel()
            sendBroadcast(posponerAlarmaIntent)
            this.finish()
        }
        bindingView.apagarAlarma.setOnClickListener {
            vibrador.cancel()
            this.finish()
        }
    }
}
