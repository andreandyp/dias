package com.andreandyp.dias.ui.state

data class AlarmUiState(
    val id: Int,
    val formattedDay: String,
    val formattedOffset: String,
    val isOn: Boolean,
    val shouldVibrate: Boolean,
    val ringtoneName: String?,
    val isConfigExpanded: Boolean = false,
)
