package com.esan.sportpro.data

import com.esan.sportpro.data.local.EjercicioEntity
import com.esan.sportpro.domain.entrenamientos.CategoriaTecnica
import com.esan.sportpro.domain.entrenamientos.EjercicioBiblioteca
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

/** Persistencia local (Room): conversión entre la fila SQLite y el modelo de la biblioteca. */
class EjercicioEntityTest {

    private val ejercicio = EjercicioBiblioteca(
        id = "e1",
        academiaId = "a1",
        nombre = "Rondo 4v2",
        objetivo = "Conservar el balón",
        categoria = CategoriaTecnica.PASE_Y_CONTROL.name,
        duracionMin = 15,
        minJugadores = 6,
        materiales = "Conos, petos",
        descripcion = "Cuatro atacantes contra dos defensores",
        imagenUrl = "https://ejemplo/imagen.jpg",
        activo = false,
        creadoPor = "u1",
    )

    @Test
    fun `ida y vuelta conserva todos los campos`() {
        assertEquals(ejercicio, EjercicioEntity.desde(ejercicio).aDominio())
    }

    @Test
    fun `la fila guarda academia y estado para las consultas SQL`() {
        val fila = EjercicioEntity.desde(ejercicio)
        assertEquals("a1", fila.academiaId)
        assertFalse(fila.activo)
        assertEquals(CategoriaTecnica.PASE_Y_CONTROL, fila.aDominio().categoriaTecnica)
    }
}
