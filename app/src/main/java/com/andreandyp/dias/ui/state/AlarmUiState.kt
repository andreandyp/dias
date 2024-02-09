package com.andreandyp.dias.ui.state

import com.andreandyp.dias.domain.OffsetType

data class AlarmUiState(
    val id: Int,
    val formattedDay: String,
    val offsetHours: Int,
    val offsetMinutes: Int,
    val offsetType: OffsetType?,
    val isOn: Boolean,
    val shouldVibrate: Boolean,
    val ringtoneName: String?,
    val ringtoneUri: String?,
    val isConfigExpanded: Boolean = false,
)
