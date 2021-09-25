package com.andreandyp.dias.bd.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "Sunrise")
data class SunriseEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Long = 0,
    @ColumnInfo(name = "sunriseDate")
    val sunriseDate: LocalDate,
    @ColumnInfo(name = "sunriseTime")
    val sunriseTime: LocalTime,
)
