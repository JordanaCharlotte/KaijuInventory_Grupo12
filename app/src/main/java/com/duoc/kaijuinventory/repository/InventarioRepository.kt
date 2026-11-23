package com.duoc.kaijuinventory.repository

import com.duoc.kaijuinventory.data.InventarioDataStore
import com.duoc.kaijuinventory.data.remote.BackendCliente
import com.duoc.kaijuinventory.data.remote.MovimientoDto
import com.duoc.kaijuinventory.data.remote.MovimientoRequestDto
import com.duoc.kaijuinventory.data.remote.MovimientosApi
import com.duoc.kaijuinventory.data.remote.ProductoDto
import com.duoc.kaijuinventory.data.remote.ProductosApi
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.TipoMovimiento
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId

// Fuente de datos de la app:
// - Los datos reales viven en los microservicios (ms-productos y ms-movimientos).
// - DataStore guarda una copia local para mostrar algo si no hay conexión.
class InventarioRepository(
    private val dataStore: InventarioDataStore,
    private val productosApi: ProductosApi = BackendCliente.productosApi,
    private val movimientosApi: MovimientosApi = BackendCliente.movimientosApi
) {

    // ---------- Datos remotos (microservicios) ----------

    // GET /api/productos. Las fotos solo existen en el teléfono, así que se recuperan de la copia local.
    suspend fun obtenerProductosRemotos(): List<Producto> {
        val fotos = obtenerProductosLocales().associate { it.id to it.fotoPath }
        return productosApi.listar().map { dto -> dto.aProducto(fotos[dto.id?.toInt()] ?: "") }
    }

    // GET /api/movimientos
    suspend fun obtenerMovimientosRemotos(): List<Movimiento> =
        movimientosApi.listar().map { it.aMovimiento() }

    // GET /api/productos/{id}: se usa para traer el stock actualizado después de un movimiento
    suspend fun obtenerProductoRemoto(id: Int, fotoPath: String): Producto =
        productosApi.buscar(id.toLong()).aProducto(fotoPath)

    // POST /api/productos. Devuelve el producto con el id que le asignó la base de datos.
    suspend fun crearProducto(producto: Producto): Producto =
        productosApi.crear(producto.aDto()).aProducto(producto.fotoPath)

    // POST /api/movimientos. El backend valida el stock y lo actualiza en ms-productos.
    suspend fun registrarMovimiento(productoId: Int, tipo: TipoMovimiento, cantidad: Int): Movimiento =
        movimientosApi.registrar(MovimientoRequestDto(productoId.toLong(), tipo.name, cantidad)).aMovimiento()

    // ---------- Copia local (DataStore) ----------

    suspend fun obtenerProductosLocales(): List<Producto> = dataStore.productos.first() ?: emptyList()

    suspend fun obtenerMovimientosLocales(): List<Movimiento> = dataStore.movimientos.first()

    suspend fun guardarCopiaLocal(productos: List<Producto>, movimientos: List<Movimiento>) {
        dataStore.guardarProductos(productos)
        dataStore.guardarMovimientos(movimientos)
    }
}

// ---------- Conversiones entre los objetos del backend y los de la app ----------

private fun ProductoDto.aProducto(fotoPath: String) = Producto(
    id = (id ?: 0L).toInt(),
    nombre = nombre,
    categoria = categoria,
    stock = stock,
    stockMinimo = stockMinimo,
    precio = precio,
    codigoBarra = codigoBarra ?: "",
    fotoPath = fotoPath
)

private fun Producto.aDto() = ProductoDto(
    id = null, // el id lo genera el backend
    nombre = nombre,
    categoria = categoria,
    precio = precio,
    stock = stock,
    stockMinimo = stockMinimo,
    codigoBarra = codigoBarra.ifBlank { null }
)

private fun MovimientoDto.aMovimiento() = Movimiento(
    id = id.toInt(),
    productoId = productoId.toInt(),
    productoNombre = productoNombre,
    tipo = TipoMovimiento.valueOf(tipo),
    cantidad = cantidad,
    // El backend envía la fecha como texto; la app la guarda en milisegundos
    fecha = LocalDateTime.parse(fecha).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
)
