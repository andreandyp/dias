package com.andreandyp.dias.preferences

import android.content.SharedPreferences
import com.andreandyp.dias.domain.Alarm
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class AlarmSharedPreferencesDataSourceTest {
    private val editor: SharedPreferences.Editor = mock {
        on { putString(any(), any()) } doReturn this.mock
        on { putInt(any(), any()) } doReturn this.mock
        on { putBoolean(any(), any()) } doReturn this.mock
    }

    private val sharedPreferences: SharedPreferences = mock {
        on { getBoolean(any(), any()) } doReturn true
        on { getString(any(), anyOrNull()) } doReturn ""
        on { getInt(any(), any()) } doReturn 0
        on { edit() } doReturn editor
    }

    private lateinit var alarmSharedPreferencesDataSource: AlarmSharedPreferencesDataSource

    @Before
    fun setUp() {
        alarmSharedPreferencesDataSource = AlarmSharedPreferencesDataSource(sharedPreferences)
    }

    @Test
    fun `fetches alarm successfully`() {
        val id = 1
        val isNextAlarm = true

        val alarm = alarmSharedPreferencesDataSource.getAlarmPreferences(id, isNextAlarm)

        verify(sharedPreferences, times(2)).getBoolean(any(), any())
        verify(sharedPreferences, times(2)).getString(any(), anyOrNull())
        verify(sharedPreferences, times(3)).getInt(any(), any())
        assertThat(alarm.id).isEqualTo(id)
        assertThat(alarm.isNextAlarm).isEqualTo(isNextAlarm)
    }

    @Test
    fun `saves boolean property successfully`() {
        val id = 0
        val on = true

        alarmSharedPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.ON, on)

        verify(editor).putBoolean("0-on", on)
        verify(editor).commit()
    }

    @Test
    fun `saves string property successfully`() {
        val id = 0
        val tone = ""

        alarmSharedPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.TONE, tone)

        verify(editor).putString("0-tone", tone)
        verify(editor).commit()
    }

    @Test
    fun `saves int property successfully`() {
        val id = 0
        val offsetHours = 0

        alarmSharedPreferencesDataSource.saveAlarmPreference(
            id,
            Alarm.Field.OFFSET_HOURS,
            offsetHours
        )

        verify(editor).putInt("0-offset_hours", offsetHours)
        verify(editor).commit()
    }
}
