package com.duoc.kaijuinventory_grupo12.model

data class ProductoFormState(
    val nombre: String = "",
    val categoria: String = "",
    val stock: String = "",
    val precio: String = "",
    val codigoBarra: String = "",

    val nombreError: String? = null,
    val categoriaError: String? = null,
    val stockError: String? = null,
    val precioError: String? = null,

    val isSaved: Boolean = false
)