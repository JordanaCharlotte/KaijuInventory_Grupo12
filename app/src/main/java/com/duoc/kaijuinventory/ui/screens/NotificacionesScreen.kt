package com.duoc.kaijuinventory.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.duoc.kaijuinventory.ui.components.DialogoPermiso
import com.duoc.kaijuinventory.ui.components.ImagenInteligente
import com.duoc.kaijuinventory.ui.theme.ColorAdvertencia
import com.duoc.kaijuinventory.ui.theme.ColorAdvertenciaSuave
import com.duoc.kaijuinventory.ui.theme.ColorExito
import com.duoc.kaijuinventory.ui.theme.ColorExitoSuave
import com.duoc.kaijuinventory.ui.theme.RojoError
import com.duoc.kaijuinventory.ui.theme.RojoErrorSuave
import com.duoc.kaijuinventory.util.imagenDeReferencia
import com.duoc.kaijuinventory.util.tienePermisoNotificaciones
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel

@Composable
fun NotificacionesScreen(viewModel: InventoryViewModel) {
    val context = LocalContext.current
    val productos by viewModel.productos.collectAsState()
    var permisoConcedido by remember { mutableStateOf(tienePermisoNotificaciones(context)) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido -> permisoConcedido = concedido }

    if (mostrarDialogo) {
        DialogoPermiso(
            icono = Icons.Default.Notifications,
            titulo = "Activar alertas de stock",
            mensaje = "Te enviaremos una notificación cuando un producto llegue a su stock mínimo, " +
                "para que puedas reponerlo a tiempo.",
            onAceptar = {
                mostrarDialogo = false
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onCancelar = { mostrarDialogo = false }
        )
    }

    // Productos que están en el stock mínimo o por debajo
    val enAlerta = productos.filter { it.stock <= it.stockMinimo }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(text = "Alertas de stock", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "Productos que necesitan reposición",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Estado del permiso de notificaciones
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (permisoConcedido) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (permisoConcedido) "Notificaciones activadas" else "Notificaciones desactivadas",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (permisoConcedido) {
                                "Te avisaremos cuando un producto llegue a su stock mínimo."
                            } else {
                                "Actívalas para recibir avisos de stock bajo."
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                if (!permisoConcedido && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Button(
                        onClick = { mostrarDialogo = true },
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Activar notificaciones")
                    }
                }
            }
        }

        item {
            Text(
                text = "Productos en alerta (${enAlerta.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (enAlerta.isEmpty()) {
            item {
                Surface(
                    color = ColorExitoSuave,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ColorExito)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Todos los productos tienen stock suficiente.", color = ColorExito)
                    }
                }
            }
        } else {
            items(enAlerta, key = { it.id }) { producto ->
                val sinStock = producto.stock == 0
                val color = if (sinStock) RojoError else ColorAdvertencia
                val fondo = if (sinStock) RojoErrorSuave else ColorAdvertenciaSuave

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImagenInteligente(
                            fotoPath = producto.fotoPath,
                            imagenReferencia = imagenDeReferencia(producto.nombre, producto.categoria),
                            tamano = 56.dp,
                            forma = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = producto.nombre, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Stock: ${producto.stock} · mínimo: ${producto.stockMinimo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(color = fondo, shape = RoundedCornerShape(50)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (sinStock) "Agotado" else "Reponer",
                                    color = color,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
