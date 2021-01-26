package com.andreandyp.dias.bd.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.andreandyp.dias.bd.entities.AmanecerEntity
import org.threeten.bp.LocalDate

@Dao
interface AmanecerDAO {
    @Insert
    fun insertarAmanecer(amanecer: AmanecerEntity)

    @Query("SELECT * FROM Tiempo WHERE amanecerFecha = :fecha")
    fun obtenerSiguienteAmanecer(fecha: LocalDate): AmanecerEntity?

    @Query("SELECT * FROM Tiempo ORDER BY amanecerFecha ASC LIMIT 1")
    fun obtenerAmanecerMasAntiguo(): AmanecerEntity

    @Query("SELECT COUNT(*) FROM Tiempo")
    fun obtenerNumeroAmaneceres(): Int

    @Delete
    fun eliminarAmanecer(amanecer: AmanecerEntity)
}