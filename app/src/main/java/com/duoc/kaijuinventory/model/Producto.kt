package com.duoc.kaijuinventory.model

data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int,
    val precio: Double,
    val codigoBarra: String = "",
    val fotoPath: String = ""
)