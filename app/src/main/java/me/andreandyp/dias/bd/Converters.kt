package me.andreandyp.dias.bd

import androidx.room.TypeConverter
import org.threeten.bp.Instant

class Converters {
    @TypeConverter
    fun instantToString(instant: Instant): String = instant.toString()

    @TypeConverter
    fun stringToInstant(string: String): Instant = Instant.parse(string)
}