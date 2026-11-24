package com.duoc.kaijuinventory.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.duoc.kaijuinventory.model.Producto

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,
    val stock: Int,
    val stockMinimo: Int,
    val precio: Double,
    val codigoBarra: String,
    val fotoPath: String
)

fun ProductoEntity.toProducto(): Producto {
    return Producto(
        id = id,
        nombre = nombre,
        categoria = categoria,
        stock = stock,
        stockMinimo = stockMinimo,
        precio = precio,
        codigoBarra = codigoBarra,
        fotoPath = fotoPath
    )
}

fun Producto.toEntity(): ProductoEntity {
    return ProductoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria,
        stock = stock,
        stockMinimo = stockMinimo,
        precio = precio,
        codigoBarra = codigoBarra,
        fotoPath = fotoPath
    )
}
