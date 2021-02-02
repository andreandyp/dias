package com.andreandyp.dias.manager

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andreandyp.dias.R
import com.andreandyp.dias.bd.DiasRepository
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.receivers.AlarmaReceiver
import com.andreandyp.dias.utils.AlarmUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoField

class DescargarDatosAmanecerWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = DiasRepository(applicationContext)

        val fusedLocationClient: FusedLocationProviderClient? =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        val permisoFineLocation = ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val permisoCoarseLocation = ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permisoOtorgado = PackageManager.PERMISSION_GRANTED

        if (permisoFineLocation != permisoOtorgado && permisoCoarseLocation != permisoOtorgado) {
            return Result.failure()
        }

        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            runBlocking {
                val amanecer = repository.obtenerAmanecerDiario(location, false)

                val preferencias = applicationContext.getSharedPreferences(
                    applicationContext.getString(R.string.preference_file), Context.MODE_PRIVATE
                )

                val esSiguienteAlarma =
                    LocalDate.now().plusDays(1)[ChronoField.DAY_OF_WEEK] == amanecer.diaSemana + 1

                val alarma = Alarma(
                    _id = amanecer.diaSemana - 1,
                    dia = amanecer.diaSemana.toString(),
                    esSiguienteAlarma = esSiguienteAlarma,
                    _encendida = preferencias.getBoolean(
                        "${amanecer.diaSemana}_on",
                        false
                    ),
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