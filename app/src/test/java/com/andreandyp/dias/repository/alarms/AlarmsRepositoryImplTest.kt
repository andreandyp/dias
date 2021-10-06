package com.andreandyp.dias.repository.alarms

import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.preferences.PreferencesMocks
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AlarmsRepositoryImplTest {
    private val alarmsPreferencesDataSource: AlarmsPreferencesDataSource = mock {
        on {
            getAlarmPreferences(
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyBoolean()
            )
        } doReturn PreferencesMocks.alarm
    }

    private val alarmId = 0

    private lateinit var repository: AlarmsRepositoryImpl

    @Before
    fun setUp() {
        repository = AlarmsRepositoryImpl(alarmsPreferencesDataSource)
    }

    @Test
    fun `gets alarm preferences`() {
        val alarmMock = PreferencesMocks.alarm
        val alarm = repository.getAlarmPreferences(alarmMock.id, alarmMock.isNextAlarm)
        assertThat(alarm.id).isEqualTo(alarmMock.id)
        assertThat(alarm.isNextAlarm).isEqualTo(alarmMock.isNextAlarm)
    }

    @Test
    fun `saves 'on' setting`() {
        val value = true
        repository.saveOnSetting(alarmId, value)
        verify(alarmsPreferencesDataSource).saveAlarmPreference(alarmId, Alarm.Field.ON, value)
    }

    @Test
    fun `saves 'vibration' setting`() {
        val value = true
        repository.saveVibrationSetting(alarmId, value)
        verify(alarmsPreferencesDataSource).saveAlarmPreference(
            alarmId,
            Alarm.Field.VIBRATION,
            value
        )
    }

    @Test
    fun `saves 'tone' setting`() {
        val value = ""
        repository.saveToneSetting(alarmId, value)
        verify(alarmsPreferencesDataSource).saveAlarmPreference(alarmId, Alarm.Field.TONE, value)
    }

    @Test
    fun `saves 'URI tone' setting`() {
        val value = ""
        repository.saveUriToneSetting(alarmId, value)
        verify(alarmsPreferencesDataSource).saveAlarmPreference(
            alarmId,
            Alarm.Field.URI_TONE,
            value
        )
    }

    @Test
    fun `saves 'offset hours' setting`() {
        val value = 1
        repository.saveOffsetHoursSetting(alarmId, value)
        verify(alarmsPreferencesDataSource).saveAlarmPreference(
            alarmId,
            Alarm.Field.OFFSET_HOURS,
            value
        )
    }

    @Test
    fun `saves 'offset minutes' setting`() {
        val value = 1
        repository.saveOffsetMinutesSetting(alarmId, value)
        verify(alarmsPreferencesDataSource).saveAlarmPreference(
            alarmId,
            Alarm.Field.OFFSET_MINUTES,
            value
        )
    }

    @Test
    fun `saves 'offset type' setting`() {
        val value = 1
        repository.saveOffsetTypeSetting(alarmId, value)
        verify(alarmsPreferencesDataSource).saveAlarmPreference(
            alarmId,
            Alarm.Field.OFFSET_TYPE,
            value
        )
    }
}