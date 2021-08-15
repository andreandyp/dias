package com.andreandyp.dias.bd

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun localDateToString(ld: LocalDate): String = ld.toString()

    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate = LocalDate.parse(string)

    @TypeConverter
    fun localTimeToString(lt: LocalTime): String = lt.toString()

    @TypeConverter
    fun stringToLocalTime(string: String): LocalTime = LocalTime.parse(string)

    @TypeConverter
    fun instantToString(instant: Instant): String = instant.toString()

    @TypeConverter
    fun stringToInstant(string: String): Instant = Instant.parse(string)
}