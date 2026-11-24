package com.duoc.kaijuinventory.repository

import com.duoc.kaijuinventory.data.local.KaijuDatabase
import com.duoc.kaijuinventory.data.local.toEntity
import com.duoc.kaijuinventory.data.local.toMovimiento
import com.duoc.kaijuinventory.data.local.toProducto
import com.duoc.kaijuinventory.data.remote.BackendCliente
import com.duoc.kaijuinventory.data.remote.MovimientoDto
import com.duoc.kaijuinventory.data.remote.MovimientoRequestDto
import com.duoc.kaijuinventory.data.remote.MovimientosApi
import com.duoc.kaijuinventory.data.remote.ProductoDto
import com.duoc.kaijuinventory.data.remote.ProductosApi
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.TipoMovimiento
import java.time.LocalDateTime
import java.time.ZoneId

class InventarioRepository(
    private val database: KaijuDatabase,
    private val productosApi: ProductosApi = BackendCliente.productosApi,
    private val movimientosApi: MovimientosApi = BackendCliente.movimientosApi
) {
    private val productoDao = database.productoDao()
    private val movimientoDao = database.movimientoDao()

    // ---------- Backend remoto ----------

    suspend fun obtenerProductosRemotos(): List<Producto> {
        val fotosLocales = obtenerProductosLocales()
            .associate { producto -> producto.id to producto.fotoPath }

        return productosApi.listar().map { dto ->
            dto.aProducto(
                fotoPath = fotosLocales[dto.id?.toInt()] ?: ""
            )
        }
    }

    suspend fun obtenerMovimientosRemotos(): List<Movimiento> {
        return movimientosApi.listar().map { dto ->
            dto.aMovimiento()
        }
    }

    suspend fun obtenerProductoRemoto(
        id: Int,
        fotoPath: String
    ): Producto {
        return productosApi.buscar(id.toLong()).aProducto(fotoPath)
    }

    suspend fun crearProducto(producto: Producto): Producto {
        return productosApi.crear(producto.aDto()).aProducto(producto.fotoPath)
    }

    suspend fun registrarMovimiento(
        productoId: Int,
        tipo: TipoMovimiento,
        cantidad: Int
    ): Movimiento {
        val solicitud = MovimientoRequestDto(
            productoId = productoId.toLong(),
            tipo = tipo.name,
            cantidad = cantidad
        )

        return movimientosApi.registrar(solicitud).aMovimiento()
    }

    // ---------- Room: copia local ----------

    suspend fun obtenerProductosLocales(): List<Producto> {
        val guardados = productoDao.obtenerTodos()

        if (guardados.isNotEmpty()) {
            return guardados.map { it.toProducto() }
        }

        val iniciales = productosIniciales()
        productoDao.guardarTodos(
            iniciales.map { it.toEntity() }
        )

        return iniciales
    }

    suspend fun obtenerMovimientosLocales(): List<Movimiento> {
        return movimientoDao.obtenerTodos().map {
            it.toMovimiento()
        }
    }

    suspend fun guardarCopiaLocal(
        productos: List<Producto>,
        movimientos: List<Movimiento>
    ) {
        productoDao.eliminarTodos()
        productoDao.guardarTodos(
            productos.map { it.toEntity() }
        )

        movimientoDao.eliminarTodos()
        movimientoDao.guardarTodos(
            movimientos.map { it.toEntity() }
        )
    }

    private fun productosIniciales(): List<Producto> {
        return listOf(
            Producto(
                id = 1,
                nombre = "Café Espresso",
                categoria = "Bebidas Calientes",
                stock = 20,
                stockMinimo = 5,
                precio = 2200.0,
                codigoBarra = "780001"
            ),
            Producto(
                id = 2,
                nombre = "Café Latte",
                categoria = "Bebidas Calientes",
                stock = 12,
                stockMinimo = 5,
                precio = 2800.0,
                codigoBarra = "780002"
            ),
            Producto(
                id = 3,
                nombre = "Cappuccino",
                categoria = "Bebidas Calientes",
                stock = 5,
                stockMinimo = 5,
                precio = 3000.0,
                codigoBarra = "780003"
            ),
            Producto(
                id = 4,
                nombre = "Té Verde",
                categoria = "Bebidas Calientes",
                stock = 25,
                stockMinimo = 8,
                precio = 1900.0,
                codigoBarra = "780004"
            ),
            Producto(
                id = 5,
                nombre = "Chocolate Caliente",
                categoria = "Bebidas Calientes",
                stock = 9,
                stockMinimo = 5,
                precio = 2900.0,
                codigoBarra = "780005"
            ),
            Producto(
                id = 6,
                nombre = "Jugo de Naranja",
                categoria = "Bebidas Frías",
                stock = 10,
                stockMinimo = 6,
                precio = 2500.0,
                codigoBarra = "780006"
            ),
            Producto(
                id = 7,
                nombre = "Café Helado",
                categoria = "Bebidas Frías",
                stock = 3,
                stockMinimo = 5,
                precio = 3200.0,
                codigoBarra = "780007"
            ),
            Producto(
                id = 8,
                nombre = "Muffin de Arándano",
                categoria = "Repostería",
                stock = 0,
                stockMinimo = 3,
                precio = 1800.0,
                codigoBarra = "780008"
            ),
            Producto(
                id = 9,
                nombre = "Muffin de Chocolate",
                categoria = "Repostería",
                stock = 7,
                stockMinimo = 3,
                precio = 1800.0,
                codigoBarra = "780009"
            ),
            Producto(
                id = 10,
                nombre = "Croissant de Mantequilla",
                categoria = "Repostería",
                stock = 15,
                stockMinimo = 5,
                precio = 1600.0,
                codigoBarra = "780010"
            ),
            Producto(
                id = 11,
                nombre = "Galleta con Chips",
                categoria = "Repostería",
                stock = 30,
                stockMinimo = 10,
                precio = 900.0,
                codigoBarra = "780011"
            ),
            Producto(
                id = 12,
                nombre = "Sándwich Jamón Queso",
                categoria = "Salados",
                stock = 0,
                stockMinimo = 4,
                precio = 3500.0,
                codigoBarra = "780012"
            )
        )
    }
}

// ---------- Conversiones ----------

private fun ProductoDto.aProducto(
    fotoPath: String
): Producto {
    return Producto(
        id = (id ?: 0L).toInt(),
        nombre = nombre,
        categoria = categoria,
        stock = stock,
        stockMinimo = stockMinimo,
        precio = precio,
        codigoBarra = codigoBarra ?: "",
        fotoPath = fotoPath
    )
}

private fun Producto.aDto(): ProductoDto {
    return ProductoDto(
        id = null,
        nombre = nombre,
        categoria = categoria,
        precio = precio,
        stock = stock,
        stockMinimo = stockMinimo,
        codigoBarra = codigoBarra.ifBlank { null }
    )
}

private fun MovimientoDto.aMovimiento(): Movimiento {
    val fechaMilisegundos = runCatching {
        LocalDateTime.parse(fecha)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }.getOrElse {
        System.currentTimeMillis()
    }

    return Movimiento(
        id = id.toInt(),
        productoId = productoId.toInt(),
        productoNombre = productoNombre,
        tipo = TipoMovimiento.valueOf(tipo),
        cantidad = cantidad,
        fecha = fechaMilisegundos
    )
}
