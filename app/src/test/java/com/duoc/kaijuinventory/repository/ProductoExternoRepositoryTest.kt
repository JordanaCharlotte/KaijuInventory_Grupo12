package com.duoc.kaijuinventory.repository

import com.duoc.kaijuinventory.data.remote.OpenFoodFactsApi
import com.duoc.kaijuinventory.data.remote.OpenFoodFactsProducto
import com.duoc.kaijuinventory.data.remote.OpenFoodFactsRespuesta
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

// Pruebas del repositorio de la API externa usando una API falsa (sin internet)
class ProductoExternoRepositoryTest {

    // API falsa: devuelve lo que le indiquemos o lanza el error que le pasemos
    private class ApiFalsa(
        private val respuesta: OpenFoodFactsRespuesta? = null,
        private val error: Exception? = null
    ) : OpenFoodFactsApi {
        var fueLlamada = false

        override suspend fun buscarProducto(codigo: String): OpenFoodFactsRespuesta {
            fueLlamada = true
            error?.let { throw it }
            return respuesta!!
        }
    }

    @Test
    fun codigoExistente_devuelveElProducto() = runTest {
        val api = ApiFalsa(
            OpenFoodFactsRespuesta(
                codigo = "3017620422003",
                estado = 1,
                producto = OpenFoodFactsProducto("Nutella", "Ferrero, Nutella", "https://imagen.jpg")
            )
        )

        val resultado = ProductoExternoRepository(api).buscarPorCodigo("3017620422003")

        assertTrue(resultado is ResultadoBusqueda.Encontrado)
        val producto = (resultado as ResultadoBusqueda.Encontrado).producto
        assertEquals("Nutella", producto.nombre)
        assertEquals("Ferrero", producto.marca)
    }

    @Test
    fun codigoQueNoExiste_devuelveNoEncontrado() = runTest {
        val api = ApiFalsa(OpenFoodFactsRespuesta("1234567", estado = 0, producto = null))

        val resultado = ProductoExternoRepository(api).buscarPorCodigo("1234567")

        assertEquals(ResultadoBusqueda.NoEncontrado, resultado)
    }

    @Test
    fun codigoConLetras_noLlamaALaApi() = runTest {
        val api = ApiFalsa()

        val resultado = ProductoExternoRepository(api).buscarPorCodigo("abc123")

        assertEquals(ResultadoBusqueda.CodigoInvalido, resultado)
        assertFalse(api.fueLlamada)
    }

    @Test
    fun productoSinNombre_usaTextoPorDefecto() = runTest {
        val api = ApiFalsa(
            OpenFoodFactsRespuesta("7801234567890", estado = 1, producto = OpenFoodFactsProducto("", null, null))
        )

        val resultado = ProductoExternoRepository(api).buscarPorCodigo("7801234567890")

        val producto = (resultado as ResultadoBusqueda.Encontrado).producto
        assertEquals("Producto sin nombre", producto.nombre)
        assertEquals("", producto.marca)
    }

    @Test
    fun sinInternet_devuelveError() = runTest {
        val api = ApiFalsa(error = IOException("sin red"))

        val resultado = ProductoExternoRepository(api).buscarPorCodigo("3017620422003")

        assertEquals(ResultadoBusqueda.Error("Sin conexión a internet"), resultado)
    }
}
