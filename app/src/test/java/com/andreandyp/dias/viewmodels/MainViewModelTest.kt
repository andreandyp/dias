package com.andreandyp.dias.viewmodels

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreandyp.dias.DiasApplication
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.mocks.ContextMocks
import com.andreandyp.dias.mocks.DomainMocks
import com.andreandyp.dias.mocks.LocationMocks
import com.andreandyp.dias.usecases.*
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

    private val context = ApplicationProvider.getApplicationContext<DiasApplication>()
    private val alarmPendingIntent = ContextMocks.getAlarmPendingIntent(context)
    private val snoozePendingIntent = ContextMocks.getSnoozePendingIntent(context)
    private val fakeLocation = LocationMocks.fakeLocation
    private val fakeSunriseLocal = DomainMocks.sunriseLocal
    private val fakeAlarm = DomainMocks.alarm

    private val getLastLocationUseCase: GetLastLocationUseCase = mock {
        onBlocking { invoke() } doReturn fakeLocation
    }

    private val getTomorrowSunriseUseCase: GetTomorrowSunriseUseCase = mock {
        onBlocking { invoke(any(), eq(true)) } doReturn DomainMocks.sunriseNetwork
        onBlocking { invoke(any(), eq(false)) } doReturn fakeSunriseLocal
        onBlocking { invoke(eq(null), any()) } doReturn fakeSunriseLocal
    }

    private val saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase = mock {
        on { invoke(any(), any()) } doAnswer {}
    }

    private val configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase = mock {
        on { invoke(any(), any()) } doReturn fakeAlarm
    }

    private val turnOnAlarmUseCase: TurnOnAlarmUseCase = mock {
        on { invoke(any(), any()) } doAnswer {}
    }

    private val turnOffAlarmUseCase: TurnOffAlarmUseCase = mock {
        on { invoke(any(), any()) } doAnswer {}
    }

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

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
        assertThat(alarmValue).isEqualTo(fakeAlarm)
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
