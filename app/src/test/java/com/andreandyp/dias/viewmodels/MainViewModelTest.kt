package com.andreandyp.dias.viewmodels

import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreandyp.dias.DiasApplication
import com.andreandyp.dias.bd.DatabaseMocks
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.network.NetworkMocks
import com.andreandyp.dias.preferences.PreferencesMocks
import com.andreandyp.dias.usecases.*
import com.andreandyp.dias.utils.Constants
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import java.time.Instant

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var getLastLocationUseCase: GetLastLocationUseCase
    private lateinit var getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase
    private lateinit var saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase
    private lateinit var configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase
    private lateinit var turnOnAlarmUseCase: TurnOnAlarmUseCase
    private lateinit var turnOffAlarmUseCase: TurnOffAlarmUseCase

    private val testDispatcher = TestCoroutineDispatcher()
    private val fakeLocation = Location("")
    private val context = ApplicationProvider.getApplicationContext<DiasApplication>()
    private val alarmPendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(),
        PendingIntent.FLAG_CANCEL_CURRENT
    )
    private val snoozePendingIntent = PendingIntent.getBroadcast(
        context,
        Constants.SNOOZE_ALARM_CODE,
        Intent(),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getLastLocationUseCase = mock {
            onBlocking { invoke() } doReturn fakeLocation
        }
        getTomorrowSunriseUseCase = mock {
            onBlocking { invoke(any(), eq(true)) } doReturn NetworkMocks.sunrise
            onBlocking { invoke(any(), eq(false)) } doReturn DatabaseMocks.sunrise
            onBlocking { invoke(eq(null), any()) } doReturn DatabaseMocks.sunrise
        }
        saveAlarmSettingsUseCase = mock {
            on { invoke(any(), any()) } doAnswer {}
        }
        configureAlarmSettingsUseCase = mock {
            on { invoke(any(), any()) } doReturn PreferencesMocks.alarm
        }
        turnOnAlarmUseCase = mock {
            on { invoke(any(), any()) } doAnswer {}
        }
        turnOffAlarmUseCase = mock {
            on { invoke(any(), any()) } doAnswer {}
        }

        mainViewModel = MainViewModel(
            getLastLocationUseCase,
            getTomorrowSunriseUseCase,
            saveAlarmSettingsUseCase,
            configureAlarmSettingsUseCase,
            turnOnAlarmUseCase,
            turnOffAlarmUseCase
        )
    }

    @Test
    fun `setups alarms on init block`() {
        verify(configureAlarmSettingsUseCase, times(7)).invoke(any(), any())
        assertThat(mainViewModel.alarms).hasSize(7)
    }

    @Test
    fun `sets next alarm`() = runBlockingTest {
        val observerAlarm = Observer<Alarm> { }
        mainViewModel.nextAlarm.observeForever(observerAlarm)

        mainViewModel.setupNextAlarm(true)
        val alarmValue = mainViewModel.nextAlarm.value
        assertThat(alarmValue).isEqualTo(PreferencesMocks.alarm)
        assertThat(alarmValue!!.isNextAlarm).isTrue()

        mainViewModel.nextAlarm.removeObserver(observerAlarm)
    }

    @Test
    fun `changes loading status when setting up next alarm`() = runBlockingTest {
        val orderLoading = mutableListOf<Boolean>()
        val observerLoading = Observer<Boolean> { orderLoading.add(it) }
        mainViewModel.isLoading.observeForever(observerLoading)

        mainViewModel.setupNextAlarm(true)
        assertThat(orderLoading.last()).isEqualTo(false)
        assertThat(orderLoading.size).isEqualTo(3)

        mainViewModel.isLoading.removeObserver(observerLoading)
    }

    @Test
    fun `gets location when permission is enabled`() = runBlockingTest {
        mainViewModel.setupNextAlarm(
            isLocationEnabled = true
        )
        verify(getLastLocationUseCase).invoke()
        verify(getTomorrowSunriseUseCase).invoke(fakeLocation, false)
    }

    @Test
    fun `doesn't get location when permission is disabled`() = runBlockingTest {
        mainViewModel.setupNextAlarm(
            isLocationEnabled = false
        )
        verifyZeroInteractions(getLastLocationUseCase)
        verify(getTomorrowSunriseUseCase).invoke(null, false)
    }

    @Test
    fun `sets data origin to internet when forced update`() = runBlockingTest {
        val forceUpdate = true
        val observerDataOrigin = Observer<Origin> {}
        mainViewModel.dataOrigin.observeForever(observerDataOrigin)
        mainViewModel.setupNextAlarm(
            isLocationEnabled = true,
            forceUpdate = forceUpdate,
        )
        verify(getTomorrowSunriseUseCase).invoke(fakeLocation, forceUpdate)
        assertThat(mainViewModel.dataOrigin.value).isEqualTo(Origin.INTERNET)

        mainViewModel.dataOrigin.removeObserver(observerDataOrigin)
    }

    @Test
    fun `sets data origin to database when no forced update`() = runBlockingTest {
        val forceUpdate = false
        val orderDataOrigin = mutableListOf<Origin>()
        val observerDataOrigin = Observer<Origin> { orderDataOrigin.add(it) }
        mainViewModel.dataOrigin.observeForever(observerDataOrigin)
        mainViewModel.setupNextAlarm(
            isLocationEnabled = true,
            forceUpdate = forceUpdate,
        )
        verify(getTomorrowSunriseUseCase).invoke(fakeLocation, forceUpdate)
        assertThat(mainViewModel.dataOrigin.value).isEqualTo(Origin.DATABASE)

        mainViewModel.dataOrigin.removeObserver(observerDataOrigin)
    }

    @Test
    fun `sets alarm ringtone and uri`() = runBlockingTest {
        val alarmId = 1
        val uriTone = Uri.parse("content://")
        val ringtone = ""

        mainViewModel.onRingtoneSelected(alarmId, uriTone, ringtone)

        assertThat(mainViewModel.alarms[alarmId].uriTone).isEqualTo(uriTone.toString())
        assertThat(mainViewModel.alarms[alarmId].tone).isEqualTo(ringtone)
    }

    @Test
    fun `turns on alarm`() = runBlockingTest {
        val instant = Instant.now()
        mainViewModel.onAlarmOn(instant, alarmPendingIntent)

        verify(turnOnAlarmUseCase).invoke(instant, alarmPendingIntent)
    }

    @Test
    fun `turns off alarm`() = runBlockingTest {
        mainViewModel.onAlarmOff(alarmPendingIntent, snoozePendingIntent)

        verify(turnOffAlarmUseCase).invoke(alarmPendingIntent, snoozePendingIntent)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
