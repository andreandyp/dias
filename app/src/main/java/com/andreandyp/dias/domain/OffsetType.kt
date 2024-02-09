package com.andreandyp.dias.domain

enum class OffsetType(val type: Int) {
    BEFORE(-1), AFTER(1);
}

val OffsetType?.asString: String
    get() = when (this) {
        OffsetType.BEFORE -> "-"
        OffsetType.AFTER -> "+"
        else -> "±"
    }
