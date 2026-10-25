package com.duoc.kaijuinventory.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.duoc.kaijuinventory.ui.theme.ColorAdvertencia
import com.duoc.kaijuinventory.ui.theme.ColorExito
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.kaijuinventory.model.Movimiento
import com.duoc.kaijuinventory.model.TipoMovimiento
import com.duoc.kaijuinventory.ui.components.CampoFormulario
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel
import com.duoc.kaijuinventory.viewmodel.MovimientoFormViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun MovimientoScreen(
    inventoryViewModel: InventoryViewModel,
    formViewModel: MovimientoFormViewModel = viewModel()
) {
    val productos by inventoryViewModel.productos.collectAsState()
    val movimientos by inventoryViewModel.movimientos.collectAsState()
    val formState by formViewModel.state.collectAsState()

    var menuAbierto by remember { mutableStateOf(false) }
    var mostrarExito by remember { mutableStateOf(false) }

    val productoSeleccionado = productos.find { it.id == formState.productoId }
    val colorExito = ColorExito

    // El botón cambia de color según el tipo de movimiento elegido
    val colorBoton by animateColorAsState(
        targetValue = when (formState.tipo) {
            TipoMovimiento.ENTRADA -> ColorExito
            TipoMovimiento.SALIDA -> ColorAdvertencia
            TipoMovimiento.VENTA -> MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = 400),
        label = "colorBotonMovimiento"
    )
    LaunchedEffect(mostrarExito) {
        if (mostrarExito) {
            delay(3000)
            mostrarExito = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "Registrar movimiento", style = MaterialTheme.typography.headlineSmall)
        }

        // Selector de producto
        item {
            Column {
                Box {
                    OutlinedButton(
                        onClick = { menuAbierto = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = productoSeleccionado?.nombre ?: "Seleccionar producto",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = menuAbierto,
                        onDismissRequest = { menuAbierto = false }
                    ) {
                        productos.forEach { producto ->
                            DropdownMenuItem(
                                text = { Text("${producto.nombre} (stock: ${producto.stock})") },
                                onClick = {
                                    formViewModel.onProductoChange(producto.id)
                                    menuAbierto = false
                                    mostrarExito = false
                                }
                            )
                        }
                    }
                }
                formState.productoError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        }

        // Tipo de movimiento
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TipoMovimiento.entries.forEach { tipo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            formViewModel.onTipoChange(tipo)
                            mostrarExito = false
                        }
                    ) {
                        RadioButton(
                            selected = formState.tipo == tipo,
                            onClick = {
                                formViewModel.onTipoChange(tipo)
                                mostrarExito = false
                            }
                        )
                        Text(tipo.etiqueta)
                    }
                }
            }
        }

        // Cantidad
        item {
            CampoFormulario(
                value = formState.cantidad,
                onValueChange = {
                    formViewModel.onCantidadChange(it)
                    mostrarExito = false
                },
                label = "Cantidad",
                icono = Icons.Default.Add,
                error = formState.cantidadError,
                keyboardType = KeyboardType.Number
            )
        }

        item {
            Button(
                onClick = {
                    val registrado = formViewModel.registrar(productos) { id, tipo, cantidad ->
                        inventoryViewModel.registrarMovimiento(id, tipo, cantidad)
                    }
                    if (registrado) mostrarExito = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar movimiento")
            }
        }

        // Mensaje de éxito animado
        item {
            AnimatedVisibility(
                visible = mostrarExito,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = colorExito
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Movimiento registrado correctamente", color = colorExito)
                }
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Text(text = "Historial de movimientos", style = MaterialTheme.typography.titleMedium)
        }

        if (movimientos.isEmpty()) {
            item {
                Text(
                    text = "Aún no hay movimientos registrados.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(movimientos, key = { it.id }) { movimiento ->
                MovimientoItem(movimiento)
            }
        }
    }
}

@Composable
private fun MovimientoItem(movimiento: Movimiento) {
    val color = when (movimiento.tipo) {
        TipoMovimiento.ENTRADA -> ColorExito
        TipoMovimiento.SALIDA -> ColorAdvertencia
        TipoMovimiento.VENTA -> MaterialTheme.colorScheme.primary
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${movimiento.tipo.etiqueta}: ${movimiento.cantidad} u. de ${movimiento.productoNombre}",
                style = MaterialTheme.typography.titleSmall,
                color = color
            )
            Text(
                text = formatearFecha(movimiento.fecha),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun formatearFecha(timestamp: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL"))
    return formato.format(Date(timestamp))
}