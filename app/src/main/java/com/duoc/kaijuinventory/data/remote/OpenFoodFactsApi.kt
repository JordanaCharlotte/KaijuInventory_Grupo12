package com.duoc.kaijuinventory.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

// Endpoint de la API externa. Retrofit crea la implementación de esta interfaz.
// "fields" pide solo los 3 campos que usamos, así la respuesta es más liviana.
// Ejemplo: https://world.openfoodfacts.org/api/v2/product/3017620422003.json?fields=product_name,brands,image_front_url
interface OpenFoodFactsApi {

    @GET("api/v2/product/{codigo}.json?fields=product_name,brands,image_front_url")
    suspend fun buscarProducto(@Path("codigo") codigo: String): OpenFoodFactsRespuesta
}
