package com.andreandyp.dias.usecases

import com.andreandyp.dias.mocks.DomainMocks
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
    private val fakeAlarm = DomainMocks.alarm

    private val alarmsRepository: AlarmsRepository = mock {
        on { getAlarmPreferences(anyInt(), anyBoolean()) } doReturn DomainMocks.alarm
    }

    private lateinit var configureAlarmSettingsUseCase: ConfigureAlarmSettingsUseCase

    @Before
    fun setUp() {
        configureAlarmSettingsUseCase = ConfigureAlarmSettingsUseCase(alarmsRepository)
    }

    @Test
    fun `returns alarm preferences`() {
        val alarmId = fakeAlarm.id
        val isNextAlarm = fakeAlarm.isNextAlarm
        val alarm = configureAlarmSettingsUseCase(alarmId, isNextAlarm)
        verify(alarmsRepository).getAlarmPreferences(alarmId, isNextAlarm)
        assertThat(alarm.id).isEqualTo(alarmId)
        assertThat(alarm.isNextAlarm).isEqualTo(isNextAlarm)
    }
}