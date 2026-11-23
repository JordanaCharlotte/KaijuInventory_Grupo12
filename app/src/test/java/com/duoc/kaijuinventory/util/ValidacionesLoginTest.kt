package com.duoc.kaijuinventory.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de las validaciones del formulario de inicio de sesión
class ValidacionesLoginTest {

    @Test
    fun correoVacio_esObligatorio() {
        assertEquals("El correo es obligatorio", validarCorreo(""))
    }

    @Test
    fun correoSinArroba_esInvalido() {
        assertEquals("Ingresa un correo válido (ejemplo: nombre@kaiju.cl)", validarCorreo("adminkaiju.cl"))
    }

    @Test
    fun correoCorrecto_noTieneError() {
        assertNull(validarCorreo("admin@kaiju.cl"))
    }

    @Test
    fun passwordCorta_tieneError() {
        assertEquals("Debe tener al menos 6 caracteres", validarPassword("abc"))
    }

    @Test
    fun passwordCorrecta_noTieneError() {
        assertNull(validarPassword("admin123"))
    }
}
