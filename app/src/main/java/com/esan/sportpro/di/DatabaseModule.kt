package com.esan.sportpro.di

import android.content.Context
import androidx.room.Room
import com.esan.sportpro.data.local.EjercicioDao
import com.esan.sportpro.data.local.SportProDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Base de datos Room y sus DAO. Las tablas son una copia local de Firestore, así que si cambia
 * el esquema sin migración se recrean ([fallbackToDestructiveMigration]) y se vuelven a llenar
 * en la siguiente sincronización.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SportProDatabase =
        Room.databaseBuilder(context, SportProDatabase::class.java, SportProDatabase.NOMBRE)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideEjercicioDao(database: SportProDatabase): EjercicioDao = database.ejercicioDao()
}
