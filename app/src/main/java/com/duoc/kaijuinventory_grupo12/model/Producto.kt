package com.duoc.kaijuinventory_grupo12.model

data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val precio: Double,
    val codigoBarra: String = ""
)