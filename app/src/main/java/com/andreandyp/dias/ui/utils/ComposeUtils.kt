package com.andreandyp.dias.ui.utils

import com.andreandyp.dias.ui.state.AlarmUiState

class ComposeUtils {
    companion object {
        val alarmUiStatePreview = AlarmUiState(
            id = 1,
            formattedDay = "Lunes",
            formattedOffset = "+/-0:00",
            isOn = true,
            shouldVibrate = false,
            ringtoneName = null,
            ringtoneUri = null,
        )
    }
}
