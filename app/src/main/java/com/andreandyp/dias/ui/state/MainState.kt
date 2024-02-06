package com.andreandyp.dias.ui.state

import com.andreandyp.dias.domain.Origin

data class MainState(
    val isOpenActionsMenu: Boolean = false,
    val nextAlarm: String? = null,
    val loading: Boolean = false,
    val origin: Origin? = null,
)
