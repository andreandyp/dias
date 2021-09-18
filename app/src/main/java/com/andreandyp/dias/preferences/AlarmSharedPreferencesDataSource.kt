package com.andreandyp.dias.preferences

import android.content.SharedPreferences
import com.andreandyp.dias.domain.Alarm
import com.andreandyp.dias.repository.alarms.AlarmsPreferencesDataSource

class AlarmSharedPreferencesDataSource(
    private val preferences: SharedPreferences
) : AlarmsPreferencesDataSource {
    override fun getAlarmPreferences(id: Int): Alarm {
        return Alarm(id).apply {
            on = preferences.getBoolean("$id-$ON", false)
            vibration = preferences.getBoolean("$id-$VIBRATION", false)
            tone = preferences.getString("$id-$TONE", null)
            uriTone = preferences.getString("$id-$URI_TONE", null)
            offsetHours = preferences.getInt("$id-$OFFSET_HOURS", 0)
            offsetMinutes = preferences.getInt("$id-$OFFSET_MINUTES", 0)
            offsetType = preferences.getInt("$id-$OFFSET_TYPE", -1)
        }
    }

    override fun saveAlarmPreference(id: Int, field: Alarm.Field, value: Any) {
        val key = when (field) {
            Alarm.Field.ON -> "$id-$ON"
            Alarm.Field.VIBRATION -> "$id-$VIBRATION"
            Alarm.Field.TONE -> "$id-$TONE"
            Alarm.Field.URI_TONE -> "$id-$URI_TONE"
            Alarm.Field.OFFSET_HOURS -> "$id-$OFFSET_HOURS"
            Alarm.Field.OFFSET_MINUTES -> "$id-$OFFSET_MINUTES"
            Alarm.Field.OFFSET_TYPE -> "$id-$OFFSET_TYPE"
        }

        with(preferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                else -> putString(key, value.toString())
            }
            commit()
        }

    }

    companion object {
        private const val ON = "on"
        private const val VIBRATION = "vibration"
        private const val TONE = "tone"
        private const val URI_TONE = "uri_tone"
        private const val OFFSET_HOURS = "offset_hours"
        private const val OFFSET_MINUTES = "offset_minutes"
        private const val OFFSET_TYPE = "offset_type"
    }
}