package com.duoc.kaijuinventory.model

data class MovimientoFormState(
    val productoId: Int? = null,
    val tipo: TipoMovimiento = TipoMovimiento.ENTRADA,
    val cantidad: String = "",

    val productoError: String? = null,
    val cantidadError: String? = null
)