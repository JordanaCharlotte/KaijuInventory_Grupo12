package com.duoc.kaijuinventory.model

// Datos de un producto obtenidos desde la API externa Open Food Facts
data class ProductoExterno(
    val codigo: String,
    val nombre: String,
    val marca: String,
    val imagenUrl: String
)
