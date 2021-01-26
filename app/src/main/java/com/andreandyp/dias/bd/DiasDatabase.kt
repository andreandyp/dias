package com.andreandyp.dias.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andreandyp.dias.bd.dao.AmanecerDAO
import com.andreandyp.dias.bd.entities.AlarmaPospuestaEntity
import com.andreandyp.dias.bd.entities.AmanecerEntity

@Database(
    entities = [
        AlarmaPospuestaEntity::class,
        AmanecerEntity::class
    ],
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
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