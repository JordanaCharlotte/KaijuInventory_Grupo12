package com.duoc.kaijuinventory.repository

import com.duoc.kaijuinventory.data.InventarioDataStore
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.Producto
import kotlinx.coroutines.flow.first

class InventarioRepository(private val dataStore: InventarioDataStore) {

    // Si no hay datos guardados (primera ejecución), carga el catálogo ficticio inicial
    suspend fun obtenerProductos(): List<Producto> {
        val guardados = dataStore.productos.first()
        if (guardados != null) return guardados

        val iniciales = productosIniciales()
        dataStore.guardarProductos(iniciales)
        return iniciales
    }

    suspend fun obtenerMovimientos(): List<Movimiento> = dataStore.movimientos.first()

    suspend fun guardarProductos(lista: List<Producto>) = dataStore.guardarProductos(lista)

    suspend fun guardarMovimientos(lista: List<Movimiento>) = dataStore.guardarMovimientos(lista)

    // Catálogo ficticio inicial (datos inventados para el trabajo)
    private fun productosIniciales(): List<Producto> = listOf(
        // Bebidas calientes
        Producto(id = 1, nombre = "Café Espresso", categoria = "Bebidas Calientes", stock = 20, stockMinimo = 5, precio = 2200.0, codigoBarra = "780001"),
        Producto(id = 2, nombre = "Café Latte", categoria = "Bebidas Calientes", stock = 12, stockMinimo = 5, precio = 2800.0, codigoBarra = "780002"),
        Producto(id = 3, nombre = "Cappuccino", categoria = "Bebidas Calientes", stock = 5, stockMinimo = 5, precio = 3000.0, codigoBarra = "780003"),
        Producto(id = 4, nombre = "Té Verde", categoria = "Bebidas Calientes", stock = 25, stockMinimo = 8, precio = 1900.0, codigoBarra = "780004"),
        Producto(id = 5, nombre = "Chocolate Caliente", categoria = "Bebidas Calientes", stock = 9, stockMinimo = 5, precio = 2900.0, codigoBarra = "780005"),
        // Bebidas frías
        Producto(id = 6, nombre = "Jugo de Naranja", categoria = "Bebidas Frías", stock = 10, stockMinimo = 6, precio = 2500.0, codigoBarra = "780006"),
        Producto(id = 7, nombre = "Café Helado", categoria = "Bebidas Frías", stock = 3, stockMinimo = 5, precio = 3200.0, codigoBarra = "780007"),
        // Repostería
        Producto(id = 8, nombre = "Muffin de Arándano", categoria = "Repostería", stock = 0, stockMinimo = 3, precio = 1800.0, codigoBarra = "780008"),
        Producto(id = 9, nombre = "Muffin de Chocolate", categoria = "Repostería", stock = 7, stockMinimo = 3, precio = 1800.0, codigoBarra = "780009"),
        Producto(id = 10, nombre = "Croissant de Mantequilla", categoria = "Repostería", stock = 15, stockMinimo = 5, precio = 1600.0, codigoBarra = "780010"),
        Producto(id = 11, nombre = "Galleta con Chips", categoria = "Repostería", stock = 30, stockMinimo = 10, precio = 900.0, codigoBarra = "780011"),
        // Salados
        Producto(id = 12, nombre = "Sándwich Jamón Queso", categoria = "Salados", stock = 0, stockMinimo = 4, precio = 3500.0, codigoBarra = "780012")
    )
}
