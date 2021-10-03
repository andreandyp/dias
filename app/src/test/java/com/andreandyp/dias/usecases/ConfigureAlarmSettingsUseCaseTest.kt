package com.andreandyp.dias.usecases

import com.andreandyp.dias.preferences.PreferencesMocks
import com.andreandyp.dias.repository.alarms.AlarmsRepository
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ConfigureAlarmSettingsUseCaseTest {

    private lateinit var alarmsRepository: AlarmsRepository

    private val configureAlarmSettingsUseCase by lazy {
        ConfigureAlarmSettingsUseCase(alarmsRepository)
    }

    @Before
    fun setUp() {
        alarmsRepository = mock {
            on { getAlarmPreferences(anyInt(), anyBoolean()) } doReturn PreferencesMocks.alarm
        }
    }

    @Test
    fun `returns alarm preferences`() {
        val alarmId = 0
        val isNextAlarm = true
        val alarm = configureAlarmSettingsUseCase(alarmId, isNextAlarm)
        verify(alarmsRepository).getAlarmPreferences(alarmId, isNextAlarm)
        assertThat(alarm.id).isEqualTo(alarmId)
        assertThat(alarm.isNextAlarm).isEqualTo(isNextAlarm)
    }
}