package com.duoc.kaijuinventory.data.remote

import com.google.gson.annotations.SerializedName

// Clases que Gson llena con el JSON de Open Food Facts.
// Todo es nullable porque la API no siempre trae todos los campos.
data class OpenFoodFactsRespuesta(
    @SerializedName("code") val codigo: String?,
    @SerializedName("status") val estado: Int?,          // 1 = encontrado, 0 = no existe
    @SerializedName("product") val producto: OpenFoodFactsProducto?
)

data class OpenFoodFactsProducto(
    @SerializedName("product_name") val nombre: String?,
    @SerializedName("brands") val marcas: String?,
    @SerializedName("image_front_url") val imagenUrl: String?
)
