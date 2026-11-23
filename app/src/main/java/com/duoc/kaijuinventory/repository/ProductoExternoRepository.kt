package com.duoc.kaijuinventory.repository

import com.duoc.kaijuinventory.data.remote.OpenFoodFactsApi
import com.duoc.kaijuinventory.data.remote.OpenFoodFactsCliente
import com.duoc.kaijuinventory.model.ProductoExterno
import retrofit2.HttpException
import java.io.IOException

// Resultados posibles de una búsqueda en la API externa
sealed class ResultadoBusqueda {
    data class Encontrado(val producto: ProductoExterno) : ResultadoBusqueda()
    data object NoEncontrado : ResultadoBusqueda()
    data object CodigoInvalido : ResultadoBusqueda()
    data class Error(val mensaje: String) : ResultadoBusqueda()
}

// Busca productos por código de barras en Open Food Facts.
// La api se recibe por constructor para poder usar una falsa en las pruebas.
class ProductoExternoRepository(
    private val api: OpenFoodFactsApi = OpenFoodFactsCliente.api
) {

    suspend fun buscarPorCodigo(codigo: String): ResultadoBusqueda {
        val codigoLimpio = codigo.trim()

        // Un código de barras válido tiene solo dígitos (entre 6 y 20)
        if (!codigoLimpio.matches(Regex("^[0-9]{6,20}$"))) {
            return ResultadoBusqueda.CodigoInvalido
        }

        return try {
            val respuesta = api.buscarProducto(codigoLimpio)
            val producto = respuesta.producto

            if (respuesta.estado != 1 || producto == null) {
                ResultadoBusqueda.NoEncontrado
            } else {
                ResultadoBusqueda.Encontrado(
                    ProductoExterno(
                        codigo = codigoLimpio,
                        nombre = producto.nombre?.takeIf { it.isNotBlank() } ?: "Producto sin nombre",
                        // "brands" puede traer varias marcas separadas por coma; nos quedamos con la primera
                        marca = producto.marcas?.split(",")?.firstOrNull()?.trim().orEmpty(),
                        imagenUrl = producto.imagenUrl.orEmpty()
                    )
                )
            }
        } catch (e: HttpException) {
            // Open Food Facts responde 404 cuando el código no existe
            if (e.code() == 404) ResultadoBusqueda.NoEncontrado
            else ResultadoBusqueda.Error("El servicio respondió con error ${e.code()}")
        } catch (e: IOException) {
            ResultadoBusqueda.Error("Sin conexión a internet")
        }
    }
}
