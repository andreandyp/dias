package com.andreandyp.dias.usecases

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.preferences.PreferencesMocks
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SaveAlarmSettingsUseCaseTest {

    private val alarmsRepository: AlarmsRepository = mock()

    private val alarm = PreferencesMocks.alarm

    private lateinit var saveAlarmSettingsUseCase: SaveAlarmSettingsUseCase

    @Before
    fun setUp() {
        saveAlarmSettingsUseCase = SaveAlarmSettingsUseCase(alarmsRepository)
    }

    @Test
    fun `saves 'on' field`() {
        saveAlarmSettingsUseCase(alarm, Alarm.Field.ON)
        verify(alarmsRepository).saveOnSetting(alarm.id, alarm.on)
    }

    @Test
    fun `saves 'vibration' field`() {
        saveAlarmSettingsUseCase(alarm, Alarm.Field.VIBRATION)
        verify(alarmsRepository).saveVibrationSetting(alarm.id, alarm.vibration)
    }

    @Test
    fun `saves 'tone' field`() {
        saveAlarmSettingsUseCase(alarm, Alarm.Field.TONE)
        verify(alarmsRepository).saveToneSetting(alarm.id, alarm.tone)
    }

    @Test
    fun `saves 'URI tone' field`() {
        saveAlarmSettingsUseCase(alarm, Alarm.Field.URI_TONE)
        verify(alarmsRepository).saveUriToneSetting(alarm.id, alarm.uriTone)
    }

    @Test
    fun `saves 'offset hours' field`() {
        saveAlarmSettingsUseCase(alarm, Alarm.Field.OFFSET_HOURS)
        verify(alarmsRepository).saveOffsetHoursSetting(alarm.id, alarm.offsetHours)
    }

    @Test
    fun `saves 'offset minutes' field`() {
        saveAlarmSettingsUseCase(alarm, Alarm.Field.OFFSET_MINUTES)
        verify(alarmsRepository).saveOffsetMinutesSetting(alarm.id, alarm.offsetMinutes)
    }

    @Test
    fun `saves 'offset type' field`() {
        saveAlarmSettingsUseCase(alarm, Alarm.Field.OFFSET_TYPE)
        verify(alarmsRepository).saveOffsetTypeSetting(alarm.id, alarm.offsetType)
    }
}