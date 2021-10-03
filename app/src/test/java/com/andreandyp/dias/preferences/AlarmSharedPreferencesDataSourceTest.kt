package com.andreandyp.dias.preferences

import android.content.SharedPreferences
import com.andreandyp.dias.domain.Alarm
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AlarmSharedPreferencesDataSourceTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val on = true
    private val vibration = true
    private val tone = ""
    private val uriTone = ""
    private val offsetHours = 0
    private val offsetMinutes = 0
    private val offsetType = -1

    private val alarmSharedPreferencesDataSource by lazy {
        AlarmSharedPreferencesDataSource(sharedPreferences)
    }

    @Before
    fun setUp() {
        editor = mock()
        sharedPreferences = mock {
            on { getBoolean("0-on", false) } doReturn on
            on { getBoolean("0-vibration", false) } doReturn vibration
            on { getString("0-tone", null) } doReturn tone
            on { getString("0-uri_tone", null) } doReturn uriTone
            on { getInt("0-offset_hours", 0) } doReturn offsetHours
            on { getInt("0-offset_minutes", 0) } doReturn offsetMinutes
            on { getInt("0-offset_type", -1) } doReturn offsetType
            on { edit() } doReturn editor
        }
    }

    @Test
    fun `fetches alarm successfully`() {
        val id = 0
        val isNextAlarm = true
        val alarm = alarmSharedPreferencesDataSource.getAlarmPreferences(id, isNextAlarm)

        assertThat(alarm.id).isEqualTo(0)
        assertThat(alarm.isNextAlarm).isEqualTo(isNextAlarm)
        assertThat(alarm.on).isEqualTo(on)
        assertThat(alarm.vibration).isEqualTo(vibration)
        assertThat(alarm.tone).isEqualTo(tone)
        assertThat(alarm.uriTone).isEqualTo(uriTone)
        assertThat(alarm.offsetHours).isEqualTo(offsetHours)
        assertThat(alarm.offsetHours).isEqualTo(offsetMinutes)
        assertThat(alarm.offsetType).isEqualTo(offsetType)
    }

    @Test
    fun `saves boolean property successfully`() {
        val id = 0
        alarmSharedPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.ON, on)
        verify(editor).putBoolean("0-on", on)
        verify(editor).commit()
    }

    @Test
    fun `saves string property successfully`() {
        val id = 0
        alarmSharedPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.TONE, tone)
        verify(editor).putString("0-tone", tone)
        verify(editor).commit()
    }

    @Test
    fun `saves int property successfully`() {
        val id = 0
        alarmSharedPreferencesDataSource.saveAlarmPreference(id, Alarm.Field.OFFSET_HOURS, offsetHours)
        verify(editor).putInt("0-offset_hours", offsetHours)
        verify(editor).commit()
    }
}
