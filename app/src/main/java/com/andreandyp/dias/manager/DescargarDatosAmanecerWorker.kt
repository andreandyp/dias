package com.andreandyp.dias.manager

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.core.app.ActivityCompat
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.andreandyp.dias.R
import com.andreandyp.dias.bd.DiasRepository
import com.andreandyp.dias.domain.Alarma
import com.andreandyp.dias.receivers.AlarmaReceiver
import com.andreandyp.dias.utils.AlarmUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoField

class DescargarDatosAmanecerWorker(context: Context, params: WorkerParameters) :
    ListenableWorker(context, params) {

    @SuppressLint("MissingPermission")
    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture { completer ->
            val repository = DiasRepository(applicationContext)
            val preferencias = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.preference_file), Context.MODE_PRIVATE
            )

            val sync2plano = preferencias.getBoolean("sync2plano", false)
            if (!sync2plano) {
                val data = workDataOf(RESULT_KEY to "Sincronización desactivada")
                return@getFuture completer.set(Result.failure(data))
            }

            val fusedLocationClient: FusedLocationProviderClient? =
                LocationServices.getFusedLocationProviderClient(applicationContext)

            if (!permisoOtorgado()) {
                val data = workDataOf(RESULT_KEY to "Sin permiso de ubicación")
                return@getFuture completer.set(Result.failure(data))
            }

            fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                CoroutineScope(Dispatchers.IO).launch {
                    val amanecer = repository.obtenerAmanecerDiario(location, true)

                    val esSiguienteAlarma =
                        LocalDate.now()
                            .plusDays(1)[ChronoField.DAY_OF_WEEK] == amanecer.diaSemana + 1

                    val alarma = Alarma(
                        id = amanecer.diaSemana - 1,
                        esSiguienteAlarma = esSiguienteAlarma,
                    ).apply {
                        encendida = preferencias.getBoolean("${id}_on", false)
                        horasDiferencia = preferencias.getInt("${id}_hr", 0)
                        minutosDiferencia = preferencias.getInt("${id}_min", 0)
                        momento = preferencias.getInt("${id}_momento", -1)
                        fechaHoraAmanecer = amanecer.fechaHoraLocal.toLocalDateTime()
                    }

                    if (alarma.encendida) {
                        establecerAlarma(alarma.id, alarma.fechaHoraSonar!!)
                    }
                }

                completer.set(Result.success())
            }?.addOnFailureListener {
                val data = workDataOf(RESULT_KEY to it.message)
                completer.set(Result.failure(data))
            }
        }
    }

    private fun permisoOtorgado(): Boolean {
        val permisoCoarseLocation = ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permisoOtorgado = PackageManager.PERMISSION_GRANTED

        return permisoCoarseLocation == permisoOtorgado
    }

    private fun establecerAlarma(idAlarma: Int, fechaHoraSonar: LocalDateTime) {
        val mostrarAlarmaIntent = Intent(applicationContext, AlarmaReceiver::class.java)
        mostrarAlarmaIntent.putExtra(
            applicationContext.getString(R.string.notif_id_intent),
            idAlarma
        )
        val mostrarAlarmaPending = PendingIntent.getBroadcast(
            applicationContext,
            idAlarma,
            mostrarAlarmaIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val horaAlarmaUTC = fechaHoraSonar.atZone(ZoneId.systemDefault())
        AlarmUtils.encenderAlarma(
            applicationContext,
            horaAlarmaUTC.toInstant(),
            mostrarAlarmaPending
        )
    }

    companion object {
        const val TAG = "DescargarDatosAmanecerWorker"
        const val RESULT_KEY = "Resultado"
    }
}