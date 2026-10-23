package com.duoc.kaijuinventory_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.kaijuinventory_grupo12.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventoryViewModel : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        cargarProductosCafeteria()
    }

    private fun cargarProductosCafeteria() {
        _productos.value = listOf(
            Producto(1, "Café Espresso", "Bebidas Calientes", 20, 2200.0, "780001"),
            Producto(2, "Café Latte", "Bebidas Calientes", 12, 2800.0, "780002"),
            Producto(3, "Cappuccino", "Bebidas Calientes", 5, 3000.0, "780003"),
            Producto(4, "Muffin de Arándano", "Repostería", 0, 1800.0, "780004")
        )
    }

    fun agregarProducto(producto: Producto) {
        _productos.value = _productos.value + producto
    }
}