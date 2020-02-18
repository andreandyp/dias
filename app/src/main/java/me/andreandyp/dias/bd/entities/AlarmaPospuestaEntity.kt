package me.andreandyp.dias.bd.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.Instant

@Entity(tableName = "Alarma_Pospuesta")
data class AlarmaPospuestaEntity(
    @PrimaryKey(autoGenerate = true)
    val _id: Long = 0,
    @ColumnInfo(name = "fecha")
    val fecha: Instant,
    @ColumnInfo(name = "veces_pospuesta")
    val vecesPospuesta: Int,
    @ColumnInfo(name = "tiempo_pospuesto")
    val tiempoPospuesto: Int
)
