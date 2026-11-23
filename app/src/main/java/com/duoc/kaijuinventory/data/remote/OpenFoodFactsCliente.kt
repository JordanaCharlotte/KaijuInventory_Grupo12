package com.duoc.kaijuinventory.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configuración de Retrofit para Open Food Facts.
// Es independiente de los microservicios propios: tiene su propia URL base.
object OpenFoodFactsCliente {

    private const val URL_BASE = "https://world.openfoodfacts.org/"

    val api: OpenFoodFactsApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApi::class.java)
    }
}
