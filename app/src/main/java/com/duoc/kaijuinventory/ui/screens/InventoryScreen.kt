package com.duoc.kaijuinventory.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.ui.components.ImagenInteligente
import com.duoc.kaijuinventory.ui.theme.ColorAdvertencia
import com.duoc.kaijuinventory.ui.theme.ColorAdvertenciaSuave
import com.duoc.kaijuinventory.ui.theme.ColorExito
import com.duoc.kaijuinventory.ui.theme.ColorExitoSuave
import com.duoc.kaijuinventory.ui.theme.RojoError
import com.duoc.kaijuinventory.ui.theme.RojoErrorSuave
import com.duoc.kaijuinventory.util.imagenDeReferencia
import com.duoc.kaijuinventory.util.normalizarTexto
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel
import java.util.Locale

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onProductoClick: (Int) -> Unit = {}
) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var categoriaElegida by remember { mutableStateOf("Todas") }

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
            // Productos que se muestran según la búsqueda y la categoría elegida
            val categorias = listOf("Todas") + productos.map { it.categoria }.distinct()
            val textoBuscado = normalizarTexto(busqueda)
            val productosFiltrados = productos.filter { producto ->
                // Se comparan los textos sin tildes, así "cafe" encuentra "Café"
                val coincideTexto = textoBuscado.isEmpty() ||
                    normalizarTexto(producto.nombre).contains(textoBuscado) ||
                    normalizarTexto(producto.categoria).contains(textoBuscado) ||
                    producto.codigoBarra.contains(textoBuscado)
                val coincideCategoria = categoriaElegida == "Todas" || producto.categoria == categoriaElegida
                coincideTexto && coincideCategoria
            }

            val cantidadSinStock = productos.count { it.stock == 0 }
            val cantidadStockBajo = productos.count { it.stock > 0 && it.stock <= it.stockMinimo }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column {
                        Text(text = "Inventario", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "${productos.size} productos registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Resumen rápido del estado del stock
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TarjetaResumen(
                            titulo = "Disponibles",
                            valor = productos.size - cantidadSinStock - cantidadStockBajo,
                            color = ColorExito,
                            fondo = ColorExitoSuave,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaResumen(
                            titulo = "Stock bajo",
                            valor = cantidadStockBajo,
                            color = ColorAdvertencia,
                            fondo = ColorAdvertenciaSuave,
                            modifier = Modifier.weight(1f)
                        )
                        TarjetaResumen(
                            titulo = "Sin stock",
                            valor = cantidadSinStock,
                            color = RojoError,
                            fondo = RojoErrorSuave,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Buscador por nombre o código de barra
                item {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        placeholder = { Text("Buscar por nombre, categoría o código") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = if (busqueda.isNotEmpty()) {
                            {
                                IconButton(onClick = { busqueda = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                // Filtro por categoría
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categorias) { categoria ->
                            FilterChip(
                                selected = categoriaElegida == categoria,
                                onClick = { categoriaElegida = categoria },
                                label = { Text(categoria) }
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Productos (${productosFiltrados.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (productosFiltrados.isEmpty()) {
                    item {
                        Text(
                            text = "No hay productos que coincidan con la búsqueda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }

                items(productosFiltrados, key = { it.id }) { producto ->
                    // animateItem: la tarjeta se mueve suave al filtrar la lista
                    Box(modifier = Modifier.animateItem()) {
                        ProductoItem(producto = producto, onClick = { onProductoClick(producto.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaResumen(
    titulo: String,
    valor: Int,
    color: Color,
    fondo: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = color
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}

@Composable
fun ProductoItem(producto: Producto, onClick: () -> Unit = {}) {
    val sinStock = producto.stock == 0
    val stockBajo = !sinStock && producto.stock <= producto.stockMinimo

    val colorEstado = when {
        sinStock -> RojoError
        stockBajo -> ColorAdvertencia
        else -> ColorExito
    }
    val fondoEstado = when {
        sinStock -> RojoErrorSuave
        stockBajo -> ColorAdvertenciaSuave
        else -> ColorExitoSuave
    }
    val textoEstado = when {
        sinStock -> "Sin stock"
        stockBajo -> "Stock bajo: ${producto.stock} (mín. ${producto.stockMinimo})"
        else -> "Disponible: ${producto.stock}"
    }
    val precioFormateado = "%,.0f".format(Locale.forLanguageTag("es-CL"), producto.precio)

    // La barra se llena según el stock; se anima cuando el stock cambia
    val progresoStock by animateFloatAsState(
        targetValue = calcularProgresoStock(producto.stock, producto.stockMinimo),
        animationSpec = tween(600),
        label = "progresoStock"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenInteligente(
                fotoPath = producto.fotoPath,
                imagenReferencia = imagenDeReferencia(producto.nombre, producto.categoria),
                tamano = 72.dp,
                forma = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$$precioFormateado",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Etiqueta con el estado del stock
                Surface(
                    color = fondoEstado,
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (sinStock || stockBajo) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colorEstado,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = textoEstado,
                            color = colorEstado,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progresoStock },
                    color = colorEstado,
                    trackColor = fondoEstado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Calcula qué tan llena va la barra: se toma como "lleno" tres veces el stock mínimo
fun calcularProgresoStock(stock: Int, stockMinimo: Int): Float {
    val referencia = maxOf(stockMinimo * 3, 1)
    return (stock.toFloat() / referencia).coerceIn(0f, 1f)
}
