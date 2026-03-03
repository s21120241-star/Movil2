package com.example.movil2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movil2.data.local.dao.Sicenetdao
import com.example.movil2.data.local.entity.*

@Database(
    entities = [
        AlumnoDB::class,
        CargaAcademicaDB::class,
        KardexDB::class,
        CalifUnidadesDB::class,
        CalifFinalDB::class
    ],
    version = 1,
    exportSchema = false
)
abstract class Sicenetdatabase : RoomDatabase() {

    abstract fun sicenetDao(): Sicenetdao

    companion object {
        @Volatile
        private var INSTANCE: Sicenetdatabase? = null

        fun getDatabase(context: Context): Sicenetdatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Sicenetdatabase::class.java,
                    "sicenet_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}