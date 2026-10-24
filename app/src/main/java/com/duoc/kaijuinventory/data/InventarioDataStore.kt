package com.duoc.kaijuinventory.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

// DataStore declarado una sola vez a nivel de archivo
private val Context.inventarioDataStore: DataStore<Preferences> by preferencesDataStore(name = "inventario_kaiju")

class InventarioDataStore(private val context: Context) {

    private val claveProductos = stringPreferencesKey("productos_json")
    private val claveMovimientos = stringPreferencesKey("movimientos_json")

    // null = todavía no se ha guardado nada (primera ejecución)
    val productos: Flow<List<Producto>?> = context.inventarioDataStore.data.map { prefs ->
        prefs[claveProductos]?.let { json -> runCatching { jsonAProductos(json) }.getOrNull() }
    }

    val movimientos: Flow<List<Movimiento>> = context.inventarioDataStore.data.map { prefs ->
        prefs[claveMovimientos]?.let { json -> runCatching { jsonAMovimientos(json) }.getOrNull() }
            ?: emptyList()
    }

    suspend fun guardarProductos(lista: List<Producto>) {
        context.inventarioDataStore.edit { prefs ->
            prefs[claveProductos] = productosAJson(lista)
        }
    }

    suspend fun guardarMovimientos(lista: List<Movimiento>) {
        context.inventarioDataStore.edit { prefs ->
            prefs[claveMovimientos] = movimientosAJson(lista)
        }
    }
}

// ---------- Conversión a/desde JSON (org.json viene incluido en Android) ----------

private fun productosAJson(lista: List<Producto>): String {
    val arreglo = JSONArray()
    lista.forEach { p ->
        arreglo.put(
            JSONObject()
                .put("id", p.id)
                .put("nombre", p.nombre)
                .put("categoria", p.categoria)
                .put("stock", p.stock)
                .put("stockMinimo", p.stockMinimo)
                .put("precio", p.precio)
                .put("codigoBarra", p.codigoBarra)
                .put("fotoPath", p.fotoPath)
        )
    }
    return arreglo.toString()
}

private fun jsonAProductos(json: String): List<Producto> {
    val arreglo = JSONArray(json)
    return List(arreglo.length()) { i ->
        val o = arreglo.getJSONObject(i)
        Producto(
            id = o.getInt("id"),
            nombre = o.getString("nombre"),
            categoria = o.getString("categoria"),
            stock = o.getInt("stock"),
            stockMinimo = o.optInt("stockMinimo", 5),
            precio = o.getDouble("precio"),
            codigoBarra = o.optString("codigoBarra", ""),
            fotoPath = o.optString("fotoPath", "")
        )
    }
}

private fun movimientosAJson(lista: List<Movimiento>): String {
    val arreglo = JSONArray()
    lista.forEach { m ->
        arreglo.put(
            JSONObject()
                .put("id", m.id)
                .put("productoId", m.productoId)
                .put("productoNombre", m.productoNombre)
                .put("tipo", m.tipo.name)
                .put("cantidad", m.cantidad)
                .put("fecha", m.fecha)
        )
    }
    return arreglo.toString()
}

private fun jsonAMovimientos(json: String): List<Movimiento> {
    val arreglo = JSONArray(json)
    return List(arreglo.length()) { i ->
        val o = arreglo.getJSONObject(i)
        Movimiento(
            id = o.getInt("id"),
            productoId = o.getInt("productoId"),
            productoNombre = o.getString("productoNombre"),
            tipo = TipoMovimiento.valueOf(o.getString("tipo")),
            cantidad = o.getInt("cantidad"),
            fecha = o.getLong("fecha")
        )
    }
}