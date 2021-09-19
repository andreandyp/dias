package com.andreandyp.dias.workmanagers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andreandyp.dias.R
import com.andreandyp.dias.receivers.AlarmaReceiver
import com.andreandyp.dias.usecases.ConfigureAlarmSettingsUseCase
import com.andreandyp.dias.usecases.GetLastLocationUseCase
import com.andreandyp.dias.usecases.GetTomorrowSunriseUseCase
import com.andreandyp.dias.usecases.TurnOnAlarmUseCase
import com.andreandyp.dias.utils.AlarmUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class DownloadSunriseWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase,
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase,
    private val turnOnAlarmUseCase: TurnOnAlarmUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val nextDay = LocalDate.now().plusDays(1).dayOfWeek
        val nextAlarm = configureAlarmSettingsUseCase(nextDay.value, true)
        val lastLocation = getLastLocationUseCase() ?: return Result.failure()
        val sunrise = getTomorrowSunriseUseCase(lastLocation, true)
        nextAlarm.utcRingingAt = sunrise.dateTimeUTC
        if (nextAlarm.on) {
            val alarmPendingIntent = AlarmUtils.createAlarmPendingIntent(context, nextAlarm.id)
            turnOnAlarmUseCase(nextAlarm.ringingAt!!.toInstant(), alarmPendingIntent)
        }
        return Result.success()
    }

    companion object {
        const val TAG = "DownloadSunriseWorker"
    }
}