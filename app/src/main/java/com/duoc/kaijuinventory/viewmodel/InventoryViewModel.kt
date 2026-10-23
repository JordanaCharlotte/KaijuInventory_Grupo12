package com.duoc.kaijuinventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.kaijuinventory.data.InventarioDataStore
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.TipoMovimiento
import com.duoc.kaijuinventory.repository.InventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// AndroidViewModel porque DataStore necesita el contexto de la aplicación
class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventarioRepository(InventarioDataStore(application))

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _productos.value = repository.obtenerProductos()
            _movimientos.value = repository.obtenerMovimientos()
            _cargando.value = false
        }
    }

    fun agregarProducto(producto: Producto) {
        val nuevoId = (_productos.value.maxOfOrNull { it.id } ?: 0) + 1
        val lista = _productos.value + producto.copy(id = nuevoId)
        _productos.value = lista
        viewModelScope.launch { repository.guardarProductos(lista) }
    }

    // La validación de campos vive en MovimientoFormViewModel; aquí solo se aplica el cambio
    fun registrarMovimiento(productoId: Int, tipo: TipoMovimiento, cantidad: Int) {
        val producto = _productos.value.find { it.id == productoId } ?: return
        val nuevoStock = if (tipo == TipoMovimiento.ENTRADA) {
            producto.stock + cantidad
        } else {
            producto.stock - cantidad
        }
        if (nuevoStock < 0) return

        val productosActualizados = _productos.value.map {
            if (it.id == productoId) it.copy(stock = nuevoStock) else it
        }
        val nuevoId = (_movimientos.value.maxOfOrNull { it.id } ?: 0) + 1
        val movimiento = Movimiento(
            id = nuevoId,
            productoId = productoId,
            productoNombre = producto.nombre,
            tipo = tipo,
            cantidad = cantidad,
            fecha = System.currentTimeMillis()
        )
        // El más reciente primero
        val movimientosActualizados = listOf(movimiento) + _movimientos.value

        _productos.value = productosActualizados
        _movimientos.value = movimientosActualizados

        viewModelScope.launch {
            repository.guardarProductos(productosActualizados)
            repository.guardarMovimientos(movimientosActualizados)
        }
    }
}