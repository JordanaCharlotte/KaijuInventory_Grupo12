package com.duoc.kaijuinventory.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duoc.kaijuinventory.model.TipoMovimiento
import com.duoc.kaijuinventory.ui.components.ImagenInteligente
import com.duoc.kaijuinventory.ui.theme.ColorAdvertencia
import com.duoc.kaijuinventory.ui.theme.ColorAdvertenciaSuave
import com.duoc.kaijuinventory.ui.theme.ColorExito
import com.duoc.kaijuinventory.ui.theme.ColorExitoSuave
import com.duoc.kaijuinventory.ui.theme.RojoError
import com.duoc.kaijuinventory.ui.theme.RojoErrorSuave
import com.duoc.kaijuinventory.util.imagenDeReferencia
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Detalle de un producto: datos, nivel de stock y su historial de movimientos (trazabilidad)
@Composable
fun ProductoDetalleScreen(
    productoId: Int,
    viewModel: InventoryViewModel
) {
    val productos by viewModel.productos.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()

    val producto = productos.find { it.id == productoId }

    if (producto == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontró el producto")
        }
        return
    }

    val historial = movimientos.filter { it.productoId == productoId }
    val totalEntradas = historial.filter { it.tipo == TipoMovimiento.ENTRADA }.sumOf { it.cantidad }
    val totalSalidas = historial.filter { it.tipo == TipoMovimiento.SALIDA }.sumOf { it.cantidad }
    val totalVendido = historial.filter { it.tipo == TipoMovimiento.VENTA }.sumOf { it.cantidad }

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

    // La barra parte en 0 y se llena al abrir la pantalla
    var mostrar by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrar = true }
    val progreso by animateFloatAsState(
        targetValue = if (mostrar) calcularProgresoStock(producto.stock, producto.stockMinimo) else 0f,
        animationSpec = tween(800),
        label = "progresoDetalle"
    )

    val precio = "%,.0f".format(Locale.forLanguageTag("es-CL"), producto.precio)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cabecera con imagen y datos principales
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImagenInteligente(
                        fotoPath = producto.fotoPath,
                        imagenReferencia = imagenDeReferencia(producto.nombre, producto.categoria),
                        tamano = 140.dp,
                        forma = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = producto.nombre, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = producto.categoria,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$$precio",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (producto.codigoBarra.isNotBlank()) {
                        Text(
                            text = "Código: ${producto.codigoBarra}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Nivel de stock
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = fondoEstado)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = producto.stock.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colorEstado
                        )
                        Text(
                            text = "  unidades disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorEstado
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progreso },
                        color = colorEstado,
                        trackColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when {
                            sinStock -> "Producto agotado: hay que reponerlo"
                            stockBajo -> "Bajo el mínimo de ${producto.stockMinimo} unidades"
                            else -> "Stock mínimo: ${producto.stockMinimo} unidades"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = colorEstado
                    )
                }
            }
        }

        // Totales de movimientos del producto
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TotalMovimiento("Entradas", "+$totalEntradas", ColorExito, Modifier.weight(1f))
                TotalMovimiento("Salidas", "-$totalSalidas", ColorAdvertencia, Modifier.weight(1f))
                TotalMovimiento("Vendidos", "$totalVendido", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            }
        }

        item {
            Text(
                text = "Historial del producto",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (historial.isEmpty()) {
            item {
                Text(
                    text = "Este producto todavía no tiene movimientos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(historial, key = { it.id }) { movimiento ->
                val color = when (movimiento.tipo) {
                    TipoMovimiento.ENTRADA -> ColorExito
                    TipoMovimiento.SALIDA -> ColorAdvertencia
                    TipoMovimiento.VENTA -> MaterialTheme.colorScheme.primary
                }
                val signo = if (movimiento.tipo == TipoMovimiento.ENTRADA) "+" else "-"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = movimiento.tipo.etiqueta, style = MaterialTheme.typography.titleSmall, color = color)
                            Text(
                                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL"))
                                    .format(Date(movimiento.fecha)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "$signo${movimiento.cantidad}",
                            style = MaterialTheme.typography.titleMedium,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalMovimiento(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = valor, style = MaterialTheme.typography.titleLarge, color = color)
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
