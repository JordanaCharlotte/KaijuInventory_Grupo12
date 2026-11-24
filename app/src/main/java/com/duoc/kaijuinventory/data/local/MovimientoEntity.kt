package com.duoc.kaijuinventory.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.TipoMovimiento

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey val id: Int,
    val productoId: Int,
    val productoNombre: String,
    val tipo: String,
    val cantidad: Int,
    val fecha: Long
)

fun MovimientoEntity.toMovimiento(): Movimiento {
    return Movimiento(
        id = id,
        productoId = productoId,
        productoNombre = productoNombre,
        tipo = TipoMovimiento.valueOf(tipo),
        cantidad = cantidad,
        fecha = fecha
    )
}

fun Movimiento.toEntity(): MovimientoEntity {
    return MovimientoEntity(
        id = id,
        productoId = productoId,
        productoNombre = productoNombre,
        tipo = tipo.name,
        cantidad = cantidad,
        fecha = fecha
    )
}
