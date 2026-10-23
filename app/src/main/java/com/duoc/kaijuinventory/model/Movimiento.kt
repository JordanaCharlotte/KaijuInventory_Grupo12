package com.duoc.kaijuinventory.model

enum class TipoMovimiento(val etiqueta: String) {
    ENTRADA("Entrada"),
    SALIDA("Salida"),
    VENTA("Venta")
}

data class Movimiento(
    val id: Int,
    val productoId: Int,
    val productoNombre: String,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val fecha: Long
)