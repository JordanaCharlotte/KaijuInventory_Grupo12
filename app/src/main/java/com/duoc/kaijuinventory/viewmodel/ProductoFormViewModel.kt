package com.duoc.kaijuinventory.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.ProductoFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductoFormViewModel : ViewModel() {

    companion object {
        private const val PRECIO_MAXIMO = 10_000_000.0
        private const val STOCK_MAXIMO = 100_000
        private const val STOCK_MINIMO_MAXIMO = 100_000
    }

    private val _formState = MutableStateFlow(ProductoFormState())
    val formState: StateFlow<ProductoFormState> = _formState.asStateFlow()

    fun onNombreChange(value: String) {
        _formState.update {
            it.copy(nombre = value, nombreError = null)
        }
    }

    fun onCategoriaChange(value: String) {
        _formState.update {
            it.copy(categoria = value, categoriaError = null)
        }
    }

    fun onStockChange(value: String) {
        _formState.update {
            it.copy(
                stock = value.filter { c -> c.isDigit() },
                stockError = null
            )
        }
    }

    fun onStockMinimoChange(value: String) {
        _formState.update {
            it.copy(
                stockMinimo = value.filter { c -> c.isDigit() },
                stockMinimoError = null
            )
        }
    }

    fun onPrecioChange(value: String) {
        _formState.update {
            it.copy(
                precio = value.filter { c -> c.isDigit() || c == '.' },
                precioError = null
            )
        }
    }

    fun onCodigoBarraChange(value: String) {
        _formState.update {
            it.copy(
                codigoBarra = value.filter { c -> c.isDigit() },
                codigoBarraError = null
            )
        }
    }

    fun onFotoChange(ruta: String) {
        _formState.update {
            it.copy(fotoPath = ruta)
        }
    }

    fun guardarProducto(
        codigosExistentes: List<String>,
        onProductoGuardado: (Producto) -> Unit
    ) {
        val estado = _formState.value

        val nombreError = when {
            estado.nombre.isBlank() -> "El nombre es obligatorio"
            estado.nombre.trim().length < 3 -> "Debe tener al menos 3 caracteres"
            else -> null
        }

        val categoriaError =
            if (estado.categoria.isBlank()) {
                "La categoría es obligatoria"
            } else {
                null
            }

        val stock = estado.stock.toIntOrNull()
        val stockMinimo = estado.stockMinimo.toIntOrNull()
        val precio = estado.precio.toDoubleOrNull()

        val stockError = when {
            stock == null -> "Ingresa el stock inicial"
            stock < 0 -> "El stock no puede ser negativo"
            stock > STOCK_MAXIMO -> "El stock máximo es $STOCK_MAXIMO unidades"
            else -> null
        }

        val stockMinimoError = when {
            stockMinimo == null -> "Ingresa el stock mínimo"
            stockMinimo < 0 -> "El stock mínimo no puede ser negativo"
            stockMinimo > STOCK_MINIMO_MAXIMO ->
                "El stock mínimo máximo es $STOCK_MINIMO_MAXIMO unidades"
            stock != null && stockMinimo > stock ->
                "No puede superar el stock inicial"
            else -> null
        }

        val precioError = when {
            precio == null -> "Ingresa un precio válido"
            !precio.isFinite() -> "El precio es demasiado grande"
            precio <= 0 -> "El precio debe ser mayor a 0"
            precio > PRECIO_MAXIMO ->
                "El precio máximo es $PRECIO_MAXIMO"
            else -> null
        }

        val codigoBarraError = when {
            estado.codigoBarra.isBlank() -> null
            estado.codigoBarra.length < 6 -> "Debe tener al menos 6 dígitos"
            estado.codigoBarra in codigosExistentes ->
                "Este código ya pertenece a otro producto"
            else -> null
        }

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
            nombreError,
            categoriaError,
            stockError,
            stockMinimoError,
            precioError,
            codigoBarraError
        ).any { it != null }

        if (hayErrores || stock == null || stockMinimo == null || precio == null) {
            return
        }

        onProductoGuardado(
            Producto(
                id = 0,
                nombre = estado.nombre.trim(),
                categoria = estado.categoria.trim(),
                stock = stock,
                stockMinimo = stockMinimo,
                precio = precio,
                codigoBarra = estado.codigoBarra,
                fotoPath = estado.fotoPath
            )
        )

        _formState.value = ProductoFormState()
    }
}
