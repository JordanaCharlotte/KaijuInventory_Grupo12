package com.duoc.kaijuinventory_grupo12.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.duoc.kaijuinventory_grupo12.model.Producto
import com.duoc.kaijuinventory_grupo12.model.ProductoFormState

class ProductoFormViewModel : ViewModel() {

    var formState by mutableStateOf(ProductoFormState())
        private set

    fun onNombreChange(value: String) {
        formState = formState.copy(nombre = value)
    }

    fun onCategoriaChange(value: String) {
        formState = formState.copy(categoria = value)
    }

    fun onStockChange(value: String) {
        formState = formState.copy(stock = value)
    }

    fun onPrecioChange(value: String) {
        formState = formState.copy(precio = value)
    }

    fun onCodigoBarraChange(value: String) {
        formState = formState.copy(codigoBarra = value)
    }

    fun guardarProducto(onProductoGuardado: (Producto) -> Unit) {
        val nombreError = if (formState.nombre.isBlank()) "El nombre es obligatorio" else null
        val stockInt = formState.stock.toIntOrNull()
        val stockError = if (stockInt == null || stockInt < 0) "Stock inválido" else null
        val precioDouble = formState.precio.toDoubleOrNull()
        val precioError = if (precioDouble == null || precioDouble < 0) "Precio inválido" else null

        formState = formState.copy(
            nombreError = nombreError,
            stockError = stockError,
            precioError = precioError
        )

        if (nombreError == null && stockError == null && precioError == null) {
            val producto = Producto(
                id = 0,
                nombre = formState.nombre,
                categoria = formState.categoria,
                stock = stockInt!!,
                precio = precioDouble!!,
                codigoBarra = formState.codigoBarra
            )
            onProductoGuardado(producto)
            formState = formState.copy(isSaved = true)
        }
    }

    fun resetForm() {
        formState = ProductoFormState()
    }
}