package me.andreandyp.dias.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import me.andreandyp.dias.bd.entities.AmanecerEntity

@Dao
interface AmanecerDAO {
    @Insert
    fun insertarAmanecer(amanecer: AmanecerEntity)

    @Query("SELECT * FROM Tiempo ORDER BY amanecer ASC LIMIT 1")
    fun obtenerAmanecerMasAntiguo(): AmanecerEntity

    @Query("SELECT COUNT(*) FROM Tiempo")
    fun obtenerNumeroAmaneceres(): Int

    @Delete
    fun eliminarAmanecer(amanecer: AmanecerEntity)
}