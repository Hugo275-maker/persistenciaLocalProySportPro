package com.esan.sportpro.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Consultas SQL sobre la tabla `ejercicios_biblioteca`. */
@Dao
interface EjercicioDao {

    /** Ejercicios de la academia por nombre; vuelve a emitir cada vez que la tabla cambia. */
    @Query("SELECT * FROM ejercicios_biblioteca WHERE academia_id = :academiaId ORDER BY nombre COLLATE NOCASE")
    fun observarPorAcademia(academiaId: String): Flow<List<EjercicioEntity>>

    @Query("SELECT COUNT(*) FROM ejercicios_biblioteca WHERE academia_id = :academiaId")
    suspend fun contar(academiaId: String): Int

    @Upsert
    suspend fun guardar(ejercicio: EjercicioEntity)

    @Upsert
    suspend fun guardarTodos(ejercicios: List<EjercicioEntity>)

    @Query("UPDATE ejercicios_biblioteca SET activo = :activo WHERE id = :id")
    suspend fun cambiarEstado(id: String, activo: Boolean)

    @Query("DELETE FROM ejercicios_biblioteca WHERE id = :id")
    suspend fun eliminar(id: String)

    @Query("DELETE FROM ejercicios_biblioteca WHERE academia_id = :academiaId")
    suspend fun eliminarDeAcademia(academiaId: String)

    /**
     * Reemplaza la copia local de la academia por lo que tiene Firestore, en una sola transacción
     * (así también desaparecen los ejercicios que otro entrenador eliminó).
     */
    @Transaction
    suspend fun reemplazarAcademia(academiaId: String, ejercicios: List<EjercicioEntity>) {
        eliminarDeAcademia(academiaId)
        guardarTodos(ejercicios)
    }
}
