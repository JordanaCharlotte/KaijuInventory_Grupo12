package com.duoc.kaijuinventory.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.duoc.kaijuinventory.ui.theme.ColorAdvertencia
import com.duoc.kaijuinventory.ui.theme.ColorExito
import androidx.compose.ui.unit.dp
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.ui.components.ImagenInteligente
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel
import java.util.Locale

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    // Transición suave entre el loader y la lista
    Crossfade(
        targetState = cargando,
        animationSpec = tween(400),
        label = "cargaInventario"
    ) { estaCargando ->
        if (estaCargando) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Inventario Kaiju",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productos, key = { it.id }) { producto ->
                        ProductoItem(producto = producto)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto) {
    val sinStock = producto.stock == 0
    val stockBajo = !sinStock && producto.stock <= producto.stockMinimo

    val colorEstado = when {
        sinStock -> MaterialTheme.colorScheme.error
        stockBajo -> ColorAdvertencia
        else -> ColorExito
    }
    val textoEstado = when {
        sinStock -> "Sin stock"
        stockBajo -> "Stock bajo: ${producto.stock} (mínimo ${producto.stockMinimo})"
        else -> "Stock disponible: ${producto.stock}"
    }
    val precioFormateado = "%,.0f".format(Locale.forLanguageTag("es-CL"), producto.precio)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenInteligente(fotoPath = producto.fotoPath, tamano = 64.dp)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Categoría: ${producto.categoria}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Precio: $$precioFormateado", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (sinStock || stockBajo) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = colorEstado,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = textoEstado,
                        color = colorEstado,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}