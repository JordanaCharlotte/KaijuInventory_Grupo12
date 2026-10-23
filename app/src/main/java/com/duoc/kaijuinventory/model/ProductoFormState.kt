package com.duoc.kaijuinventory.model

data class ProductoFormState(
    val nombre: String = "",
    val categoria: String = "",
    val stock: String = "",
    val stockMinimo: String = "",
    val precio: String = "",
    val codigoBarra: String = "",

    val nombreError: String? = null,
    val categoriaError: String? = null,
    val stockError: String? = null,
    val stockMinimoError: String? = null,
    val precioError: String? = null,
    val codigoBarraError: String? = null
)