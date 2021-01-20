package me.andreandyp.dias.bd

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

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