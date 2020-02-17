package me.andreandyp.dias.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.andreandyp.dias.bd.dao.AmanecerDAO
import me.andreandyp.dias.bd.entities.AlarmaPospuestaEntity
import me.andreandyp.dias.bd.entities.AmanecerEntity

@Database(
    entities = [
        AlarmaPospuestaEntity::class,
        AmanecerEntity::class
    ],
    version = 0
)
abstract class DiasDatabase : RoomDatabase() {
    abstract fun tiempoDao(): AmanecerDAO

    companion object {
        private var INSTANCE: DiasDatabase? = null

        fun getDatabase(context: Context): DiasDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance =
                    Room.databaseBuilder(context, DiasDatabase::class.java, "dias_database").build()

                INSTANCE = instance

                return instance
            }
        }
    }
}