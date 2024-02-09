package com.andreandyp.dias.ui.utils

import com.andreandyp.dias.ui.state.AlarmUiState

class ComposeUtils {
    companion object {
        val alarmUiStatePreview = AlarmUiState(
            id = 1,
            formattedDay = "Lunes",
            offsetHours = 0,
            offsetMinutes = 0,
            offsetType = null,
            isOn = true,
            shouldVibrate = false,
            ringtoneName = null,
            ringtoneUri = null,
        )
    }
}
