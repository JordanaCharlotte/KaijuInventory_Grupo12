package com.duoc.kaijuinventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.kaijuinventory.data.InventarioDataStore
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

// AndroidViewModel porque DataStore y las notificaciones necesitan el contexto de la aplicación
class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventarioRepository(InventarioDataStore(application))

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // true cuando no se pudo hablar con el backend y se muestra la copia local
    private val _sinConexion = MutableStateFlow(false)
    val sinConexion: StateFlow<Boolean> = _sinConexion.asStateFlow()

    private val _mensajeConexion = MutableStateFlow("")
    val mensajeConexion: StateFlow<String> = _mensajeConexion.asStateFlow()

    init {
        cargarDatos()
    }

    // Trae productos y movimientos desde los microservicios
    fun cargarDatos() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                _productos.value = repository.obtenerProductosRemotos()
                _movimientos.value = repository.obtenerMovimientosRemotos()
                _sinConexion.value = false
                repository.guardarCopiaLocal(_productos.value, _movimientos.value)
            } catch (e: IOException) {
                usarCopiaLocal(e)
            } catch (e: HttpException) {
                usarCopiaLocal(e)
            }
            _cargando.value = false
        }
    }

    // Sin backend: se muestra lo último que se guardó en el teléfono
    private suspend fun usarCopiaLocal(error: Exception) {
        _productos.value = repository.obtenerProductosLocales()
        _movimientos.value = repository.obtenerMovimientosLocales()
        _sinConexion.value = true
        _mensajeConexion.value = mensajeDeError(error)
    }

    // Envía el producto nuevo a ms-productos. alTerminar recibe null si salió bien o el mensaje de error.
    fun agregarProducto(producto: Producto, alTerminar: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val creado = repository.crearProducto(producto)
                _productos.value = _productos.value + creado
                repository.guardarCopiaLocal(_productos.value, _movimientos.value)
                alTerminar(null)
            } catch (e: IOException) {
                alTerminar(mensajeDeError(e))
            } catch (e: HttpException) {
                alTerminar(mensajeDeError(e))
            }
        }
    }

    // Envía el movimiento a ms-movimientos, que actualiza el stock en ms-productos.
    // La validación de los campos vive en MovimientoFormViewModel.
    fun registrarMovimiento(productoId: Int, tipo: TipoMovimiento, cantidad: Int, alTerminar: (String?) -> Unit) {
        val producto = _productos.value.find { it.id == productoId } ?: return

        viewModelScope.launch {
            try {
                val movimiento = repository.registrarMovimiento(productoId, tipo, cantidad)
                // Se pide el producto al backend para mostrar el stock real ya actualizado
                val actualizado = repository.obtenerProductoRemoto(productoId, producto.fotoPath)

                _productos.value = _productos.value.map { if (it.id == productoId) actualizado else it }
                _movimientos.value = listOf(movimiento) + _movimientos.value // el más reciente primero
                repository.guardarCopiaLocal(_productos.value, _movimientos.value)

                // Recurso nativo: si el stock queda en el mínimo o bajo él, se avisa con una notificación
                if (tipo != TipoMovimiento.ENTRADA && actualizado.stock <= actualizado.stockMinimo) {
                    notificarStockBajo(getApplication<Application>(), actualizado)
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
