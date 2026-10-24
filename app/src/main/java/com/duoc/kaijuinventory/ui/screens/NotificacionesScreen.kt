package com.duoc.kaijuinventory.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.duoc.kaijuinventory.util.tienePermisoNotificaciones
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel

@Composable
fun NotificacionesScreen(viewModel: InventoryViewModel) {
    val context = LocalContext.current
    val productos by viewModel.productos.collectAsState()
    var permisoConcedido by remember { mutableStateOf(tienePermisoNotificaciones(context)) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido -> permisoConcedido = concedido }

    // Productos que están en el stock mínimo o por debajo
    val enAlerta = productos.filter { it.stock <= it.stockMinimo }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "Alertas de stock", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Text(
                text = if (permisoConcedido) {
                    "Las notificaciones están activadas."
                } else {
                    "Las notificaciones están desactivadas."
                }
            )
        }

        if (!permisoConcedido && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                Button(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                    Text("Activar notificaciones")
                }
            }
        }

        item {
            Text(
                text = "Productos en el stock mínimo o por debajo",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (enAlerta.isEmpty()) {
            item { Text("Todos los productos tienen stock suficiente.") }
        } else {
            items(enAlerta, key = { it.id }) { producto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = producto.nombre, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "Stock: ${producto.stock} (mínimo ${producto.stockMinimo})",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}