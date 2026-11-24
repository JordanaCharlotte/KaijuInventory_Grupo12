package com.duoc.kaijuinventory.data.remote

data class ProductoDto(
    val id: Int? = null,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int,
    val precio: Double,
    val codigoBarra: String = "",
    val fotoPath: String = ""
)
