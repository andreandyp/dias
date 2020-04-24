package me.andreandyp.dias

import android.app.Application
import androidx.work.*
import com.jakewharton.threetenabp.AndroidThreeTen
import me.andreandyp.dias.manager.DescargarDatosAmanecerWorker
import java.util.concurrent.TimeUnit

class DiasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        val limites = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<DescargarDatosAmanecerWorker>(
            1,
            TimeUnit.DAYS
        ).setConstraints(limites).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DescargarDatosAmanecerWorker.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}