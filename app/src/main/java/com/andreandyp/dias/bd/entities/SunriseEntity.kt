package com.andreandyp.dias.bd.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreandyp.dias.domain.Origin
import com.andreandyp.dias.domain.Sunrise
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Entity(tableName = "Sunrise")
data class SunriseEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Long = 0,
    @ColumnInfo(name = "sunriseDate")
    val sunriseDate: LocalDate,
    @ColumnInfo(name = "sunriseTime")
    val sunriseTime: LocalTime,
)

fun SunriseEntity.asDomain(): Sunrise {
    val dateTimeUTC = ZonedDateTime.of(
        sunriseDate,
        sunriseTime,
        ZoneOffset.ofOffset("", ZoneOffset.UTC)
    )

    return Sunrise(
        dayOfWeek = dateTimeUTC.dayOfWeek,
        dateTimeUTC = dateTimeUTC,
        origin = Origin.DATABASE
    )
}

fun Sunrise.asEntity(): SunriseEntity {
    val sunriseDate = dateTimeUTC.toLocalDate()
    val sunriseTime = dateTimeUTC.toLocalTime()
    return SunriseEntity(
        sunriseDate = sunriseDate,
        sunriseTime = sunriseTime,
    )
}