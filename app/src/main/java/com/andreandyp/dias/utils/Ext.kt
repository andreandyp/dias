package com.andreandyp.dias.utils

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

fun DayOfWeek.translateDisplayName(locale: Locale = Locale.getDefault()): String {
    return this.getDisplayName(TextStyle.FULL, locale).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale) else it.toString()
    }
}