package com.andreandyp.dias.bd

import androidx.room.TypeConverter
import com.andreandyp.dias.domain.OffsetType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

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

    @TypeConverter
    fun offsetTypeToInt(offsetType: OffsetType?): Int? = offsetType?.type

    @TypeConverter
    fun intToOffsetType(value: Int?): OffsetType? = OffsetType.entries.find { it.type == value }

    @TypeConverter
    fun offsetDateTimeToString(dateTime: OffsetDateTime?): String? = dateTime?.toString()

    @TypeConverter
    fun stringToOffsetDateTime(value: String?): OffsetDateTime? = value?.let { OffsetDateTime.parse(it) }
}
