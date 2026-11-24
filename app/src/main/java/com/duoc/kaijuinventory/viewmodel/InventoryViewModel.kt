package com.duoc.kaijuinventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.kaijuinventory.data.local.KaijuDatabase
import com.duoc.kaijuinventory.data.remote.mensajeDeError
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.TipoMovimiento
import com.duoc.kaijuinventory.repository.InventarioRepository
import com.duoc.kaijuinventory.util.notificarStockBajo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class InventoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = KaijuDatabase.obtenerBaseDeDatos(application)
    private val repository = InventarioRepository(database)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _actualizando = MutableStateFlow(false)
    val actualizando: StateFlow<Boolean> = _actualizando.asStateFlow()

    private val _sinConexion = MutableStateFlow(false)
    val sinConexion: StateFlow<Boolean> = _sinConexion.asStateFlow()

    private val _mensajeConexion = MutableStateFlow("")
    val mensajeConexion: StateFlow<String> = _mensajeConexion.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _cargando.value = true

            try {
                _productos.value = repository.obtenerProductosRemotos()
                _movimientos.value = repository.obtenerMovimientosRemotos()

                repository.guardarCopiaLocal(
                    _productos.value,
                    _movimientos.value
                )

                _sinConexion.value = false
                _mensajeConexion.value = ""
            } catch (e: IOException) {
                usarCopiaLocal(e)
            } catch (e: HttpException) {
                usarCopiaLocal(e)
            }

            _cargando.value = false
        }
    }

    fun actualizar() {
        viewModelScope.launch {
            _actualizando.value = true

            try {
                _productos.value = repository.obtenerProductosRemotos()
                _movimientos.value = repository.obtenerMovimientosRemotos()

                repository.guardarCopiaLocal(
                    _productos.value,
                    _movimientos.value
                )

                _sinConexion.value = false
                _mensajeConexion.value = ""
            } catch (e: IOException) {
                usarCopiaLocal(e)
            } catch (e: HttpException) {
                usarCopiaLocal(e)
            }

            _actualizando.value = false
        }
    }

    private suspend fun usarCopiaLocal(error: Exception) {
        _productos.value = repository.obtenerProductosLocales()
        _movimientos.value = repository.obtenerMovimientosLocales()
        _sinConexion.value = true
        _mensajeConexion.value = mensajeDeError(error)
    }

    fun agregarProducto(
        producto: Producto,
        alTerminar: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val creado = repository.crearProducto(producto)
                _productos.value = _productos.value + creado

                repository.guardarCopiaLocal(
                    _productos.value,
                    _movimientos.value
                )

                alTerminar(null)
            } catch (e: IOException) {
                alTerminar(mensajeDeError(e))
            } catch (e: HttpException) {
                alTerminar(mensajeDeError(e))
            }
        }
    }

    fun registrarMovimiento(
        productoId: Int,
        tipo: TipoMovimiento,
        cantidad: Int,
        alTerminar: (String?) -> Unit
    ) {
        val producto = _productos.value.find {
            it.id == productoId
        }

        if (producto == null) {
            alTerminar("No se encontró el producto")
            return
        }

        viewModelScope.launch {
            try {
                val movimiento = repository.registrarMovimiento(
                    productoId,
                    tipo,
                    cantidad
                )

                val actualizado = repository.obtenerProductoRemoto(
                    productoId,
                    producto.fotoPath
                )

                _productos.value = _productos.value.map {
                    if (it.id == productoId) actualizado else it
                }

                _movimientos.value =
                    listOf(movimiento) + _movimientos.value

                repository.guardarCopiaLocal(
                    _productos.value,
                    _movimientos.value
                )

                if (
                    tipo != TipoMovimiento.ENTRADA &&
                    actualizado.stock <= actualizado.stockMinimo
                ) {
                    notificarStockBajo(
                        getApplication<Application>(),
                        actualizado
                    )
                }

                alTerminar(null)
            } catch (e: IOException) {
                alTerminar(mensajeDeError(e))
            } catch (e: HttpException) {
                alTerminar(mensajeDeError(e))
            }
        }
    }
}
