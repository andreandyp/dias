package me.andreandyp.dias.manager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking
import me.andreandyp.dias.R
import me.andreandyp.dias.bd.DiasRepository
import me.andreandyp.dias.domain.Alarma
import me.andreandyp.dias.receivers.AlarmaReceiver
import me.andreandyp.dias.utils.AlarmUtils
import org.threeten.bp.ZoneId

class DescargarDatosAmanecerWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = DiasRepository(applicationContext)

        val fusedLocationClient: FusedLocationProviderClient? =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            runBlocking {
                val amanecer = repository.obtenerAmanecerDiario(location)

                val preferencias = applicationContext.getSharedPreferences(
                    applicationContext.getString(R.string.preference_file), Context.MODE_PRIVATE
                )

                val alarma = Alarma(
                    _id = amanecer.diaSemana - 1,
                    dia = amanecer.diaSemana.toString(),
                    _encendida = preferencias.getBoolean("${amanecer.diaSemana}_on", false),
                    _vibrar = preferencias.getBoolean("${amanecer.diaSemana}_vib", false),
                    _horasDiferencia = preferencias.getInt("${amanecer.diaSemana}_hr", 0),
                    _minutosDiferencia = preferencias.getInt("${amanecer.diaSemana}_min", 0),
                    _momento = preferencias.getInt("${amanecer.diaSemana}_momento", -1)
                )

                if (alarma.encendida) {
                    val mostrarAlarmaIntent = Intent(applicationContext, AlarmaReceiver::class.java)
                    mostrarAlarmaIntent.putExtra(
                        applicationContext.getString(R.string.notif_id_intent),
                        alarma._id
                    )
                    val mostrarAlarmaPending = PendingIntent.getBroadcast(
                        applicationContext,
                        alarma._id,
                        mostrarAlarmaIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )

                    val horaAlarmaUTC = alarma.fechaHoraSonar!!.atZone(ZoneId.systemDefault())
                    AlarmUtils.encenderAlarma(
                        applicationContext,
                        horaAlarmaUTC.toInstant(),
                        mostrarAlarmaPending
                    )
                }
            }
        }
        return Result.success()
    }

    companion object {
        const val TAG = "DescargarDatosAmanecerWorker"
    }
}