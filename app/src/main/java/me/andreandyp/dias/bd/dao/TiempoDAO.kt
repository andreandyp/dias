package me.andreandyp.dias.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.andreandyp.dias.bd.entities.TiempoEntity

@Dao
interface TiempoDAO {
    @Insert
    fun insertarAmanecer(tiempo: TiempoEntity)

    @Query("SELECT * FROM Tiempo ORDER BY amanecer ASC LIMIT 1")
    fun obtenerAmanecerMasAntiguo(): TiempoEntity

    @Delete
    fun eliminarAmanecerMasAntiguo(tiempo: TiempoEntity)
}