package com.duoc.kaijuinventory.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.kaijuinventory.model.MovimientoFormState
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MovimientoFormViewModel : ViewModel() {

    companion object {
        private const val CANTIDAD_MAXIMA = 100_000
    }

    private val _state = MutableStateFlow(MovimientoFormState())
    val state: StateFlow<MovimientoFormState> = _state.asStateFlow()

    fun onProductoChange(id: Int) {
        _state.update {
            it.copy(
                productoId = id,
                productoError = null,
                cantidadError = null
            )
        }
    }

    fun onTipoChange(tipo: TipoMovimiento) {
        _state.update {
            it.copy(
                tipo = tipo,
                cantidadError = null
            )
        }
    }

    fun onCantidadChange(value: String) {
        _state.update {
            it.copy(
                cantidad = value.filter { c -> c.isDigit() },
                cantidadError = null
            )
        }
    }

    fun registrar(
        productos: List<Producto>,
        onValido: (productoId: Int, tipo: TipoMovimiento, cantidad: Int) -> Unit
    ): Boolean {
        val estado = _state.value

        val producto = productos.find {
            it.id == estado.productoId
        }

        val productoError = if (producto == null) {
            "Selecciona un producto"
        } else {
            null
        }

        val cantidad = estado.cantidad.toIntOrNull()

        val cantidadError = when {
            cantidad == null -> {
                "Ingresa una cantidad"
            }

            cantidad <= 0 -> {
                "La cantidad debe ser mayor a 0"
            }

            cantidad > CANTIDAD_MAXIMA -> {
                "La cantidad máxima es $CANTIDAD_MAXIMA unidades"
            }

            producto != null &&
                    estado.tipo != TipoMovimiento.ENTRADA &&
                    cantidad > producto.stock -> {
                "Stock insuficiente. Disponible: ${producto.stock}"
            }

            else -> {
                null
            }
        }

        _state.update {
            it.copy(
                productoError = productoError,
                cantidadError = cantidadError
            )
        }

        if (
            producto == null ||
            cantidad == null ||
            productoError != null ||
            cantidadError != null
        ) {
            return false
        }

        onValido(
            producto.id,
            estado.tipo,
            cantidad
        )

        _state.update {
            it.copy(cantidad = "")
        }

        return true
    }
}
