package com.esan.sportpro.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.esan.sportpro.domain.entrenamientos.EjercicioBiblioteca

/**
 * Fila de la tabla SQLite `ejercicios_biblioteca`: copia local de un documento de la colección
 * Firestore `ejercicios` (US-009). La pantalla de la biblioteca lee siempre de esta tabla, así
 * que los ejercicios se ven y se buscan aunque no haya conexión.
 */
@Entity(
    tableName = "ejercicios_biblioteca",
    indices = [Index("academia_id")],
)
data class EjercicioEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "academia_id")
    val academiaId: String,
    val nombre: String,
    val objetivo: String,
    val categoria: String,
    @ColumnInfo(name = "duracion_min")
    val duracionMin: Int,
    @ColumnInfo(name = "min_jugadores")
    val minJugadores: Int,
    val materiales: String,
    val descripcion: String,
    @ColumnInfo(name = "imagen_url")
    val imagenUrl: String,
    val activo: Boolean,
    @ColumnInfo(name = "creado_por")
    val creadoPor: String,
) {
    fun aDominio(): EjercicioBiblioteca = EjercicioBiblioteca(
        id = id,
        academiaId = academiaId,
        nombre = nombre,
        objetivo = objetivo,
        categoria = categoria,
        duracionMin = duracionMin,
        minJugadores = minJugadores,
        materiales = materiales,
        descripcion = descripcion,
        imagenUrl = imagenUrl,
        activo = activo,
        creadoPor = creadoPor,
    )

    companion object {
        fun desde(ejercicio: EjercicioBiblioteca): EjercicioEntity = EjercicioEntity(
            id = ejercicio.id,
            academiaId = ejercicio.academiaId,
            nombre = ejercicio.nombre,
            objetivo = ejercicio.objetivo,
            categoria = ejercicio.categoria,
            duracionMin = ejercicio.duracionMin,
            minJugadores = ejercicio.minJugadores,
            materiales = ejercicio.materiales,
            descripcion = ejercicio.descripcion,
            imagenUrl = ejercicio.imagenUrl,
            activo = ejercicio.activo,
            creadoPor = ejercicio.creadoPor,
        )
    }
}
