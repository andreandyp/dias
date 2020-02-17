package me.andreandyp.dias.bd.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "Tiempo")
data class AmanecerEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Long = 0,
    @ColumnInfo(name = "amanecer")
    val amanecerFechaHora: Instant
)
