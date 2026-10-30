package com.esan.sportpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Base de datos SQLite local de la app (`sportpro.db`), administrada con Room. */
@Database(
    entities = [EjercicioEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SportProDatabase : RoomDatabase() {
    abstract fun ejercicioDao(): EjercicioDao

    companion object {
        const val NOMBRE = "sportpro.db"
    }
}
