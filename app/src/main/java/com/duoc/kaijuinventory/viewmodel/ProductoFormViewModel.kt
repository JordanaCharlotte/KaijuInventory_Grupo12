package com.duoc.kaijuinventory.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.ProductoFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductoFormViewModel : ViewModel() {

    private val _formState = MutableStateFlow(ProductoFormState())
    val formState: StateFlow<ProductoFormState> = _formState.asStateFlow()

    fun onNombreChange(value: String) {
        _formState.update { it.copy(nombre = value, nombreError = null) }
    }

    fun onCategoriaChange(value: String) {
        _formState.update { it.copy(categoria = value, categoriaError = null) }
    }

    fun onStockChange(value: String) {
        _formState.update { it.copy(stock = value.filter { c -> c.isDigit() }, stockError = null) }
    }

    fun onStockMinimoChange(value: String) {
        _formState.update {
            it.copy(stockMinimo = value.filter { c -> c.isDigit() }, stockMinimoError = null)
        }
    }

    fun onPrecioChange(value: String) {
        _formState.update {
            it.copy(precio = value.filter { c -> c.isDigit() || c == '.' }, precioError = null)
        }
    }

    fun onCodigoBarraChange(value: String) {
        _formState.update {
            it.copy(codigoBarra = value.filter { c -> c.isDigit() }, codigoBarraError = null)
        }
    }

    // Valida todo el formulario; si es válido entrega el producto y limpia el formulario
    fun guardarProducto(onProductoGuardado: (Producto) -> Unit) {
        val estado = _formState.value

        val nombreError = when {
            estado.nombre.isBlank() -> "El nombre es obligatorio"
            estado.nombre.trim().length < 3 -> "Debe tener al menos 3 caracteres"
            else -> null
        }
        val categoriaError = if (estado.categoria.isBlank()) "La categoría es obligatoria" else null

        val stock = estado.stock.toIntOrNull()
        val stockError = if (stock == null) "Ingresa el stock inicial (número entero)" else null

        val stockMinimo = estado.stockMinimo.toIntOrNull()
        val stockMinimoError = if (stockMinimo == null) "Ingresa el stock mínimo" else null

        val precio = estado.precio.toDoubleOrNull()
        val precioError = if (precio == null || precio <= 0) "Ingresa un precio mayor a 0" else null

        val codigoBarraError =
            if (estado.codigoBarra.isNotBlank() && estado.codigoBarra.length < 6) {
                "Debe tener al menos 6 dígitos"
            } else null

        _formState.update {
            it.copy(
                nombreError = nombreError,
                categoriaError = categoriaError,
                stockError = stockError,
                stockMinimoError = stockMinimoError,
                precioError = precioError,
                codigoBarraError = codigoBarraError
            )
        }

        val hayErrores = listOf(
            nombreError, categoriaError, stockError,
            stockMinimoError, precioError, codigoBarraError
        ).any { it != null }

        if (hayErrores || stock == null || stockMinimo == null || precio == null) return

        onProductoGuardado(
            Producto(
                id = 0, // el id real lo asigna InventoryViewModel
                nombre = estado.nombre.trim(),
                categoria = estado.categoria.trim(),
                stock = stock,
                stockMinimo = stockMinimo,
                precio = precio,
                codigoBarra = estado.codigoBarra
            )
        )
        _formState.value = ProductoFormState()
    }
}