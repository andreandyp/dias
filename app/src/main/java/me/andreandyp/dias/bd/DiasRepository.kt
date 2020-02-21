package me.andreandyp.dias.bd

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.andreandyp.dias.bd.dao.AmanecerDAO
import me.andreandyp.dias.bd.entities.AmanecerEntity
import me.andreandyp.dias.network.AmanecerNetwork
import me.andreandyp.dias.network.SunriseSunsetAPI

class DiasRepository(application: Application) {
    private val amanecerDAO: AmanecerDAO

    init {
        val db = DiasDatabase.getDatabase(application.applicationContext)
        amanecerDAO = db.tiempoDao()
    }

    suspend fun obtenerAmanecerDiario(latitud: String, longitud: String): AmanecerNetwork =
        withContext(Dispatchers.IO) {
            SunriseSunsetAPI.sunriseSunsetService.obtenerAmanecer(latitud, longitud)
        }

    suspend fun insertarAmanecer(amanecerEntity: AmanecerEntity) {
        withContext(Dispatchers.IO) {
            val amaneceres = amanecerDAO.obtenerNumeroAmaneceres()
            if(amaneceres >= 30) {
                val masAntiguo = amanecerDAO.obtenerAmanecerMasAntiguo()
                amanecerDAO.eliminarAmanecer(masAntiguo)
            }
            else {
                amanecerDAO.insertarAmanecer(amanecerEntity)
            }
        }
    }
}