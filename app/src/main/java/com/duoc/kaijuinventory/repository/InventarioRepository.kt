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

    private fun productosIniciales(): List<Producto> = listOf(
        Producto(id = 1, nombre = "Café Espresso", categoria = "Bebidas Calientes", stock = 20, stockMinimo = 5, precio = 2200.0, codigoBarra = "780001"),
        Producto(id = 2, nombre = "Café Latte", categoria = "Bebidas Calientes", stock = 12, stockMinimo = 5, precio = 2800.0, codigoBarra = "780002"),
        Producto(id = 3, nombre = "Cappuccino", categoria = "Bebidas Calientes", stock = 5, stockMinimo = 5, precio = 3000.0, codigoBarra = "780003"),
        Producto(id = 4, nombre = "Muffin de Arándano", categoria = "Repostería", stock = 0, stockMinimo = 3, precio = 1800.0, codigoBarra = "780004")
    )
}