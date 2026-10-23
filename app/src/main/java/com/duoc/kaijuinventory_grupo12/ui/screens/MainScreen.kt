package com.duoc.kaijuinventory_grupo12.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.duoc.kaijuinventory_grupo12.ui.screens.InventoryScreen
import com.duoc.kaijuinventory_grupo12.ui.screens.ProductoFormScreen
import com.duoc.kaijuinventory_grupo12.viewmodel.InventoryViewModel

@Composable
fun MainScreen(viewModel: InventoryViewModel) {
    // Guardamos cuál pestaña está seleccionada
    var pestañaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestañaSeleccionada == 0,
                    onClick = { pestañaSeleccionada = 0 },
                    icon = { Text("📦") },
                    label = { Text("Inventario") }
                )
                NavigationBarItem(
                    selected = pestañaSeleccionada == 1,
                    onClick = { pestañaSeleccionada = 1 },
                    icon = { Text("➕") },
                    label = { Text("Agregar") }
                )
                NavigationBarItem(
                    selected = pestañaSeleccionada == 2,
                    onClick = { pestañaSeleccionada = 2 },
                    icon = { Text("🔔") },
                    label = { Text("Alertas") }
                )
                NavigationBarItem(
                    selected = pestañaSeleccionada == 3,
                    onClick = { pestañaSeleccionada = 3 },
                    icon = { Text("📷") },
                    label = { Text("Escáner") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (pestañaSeleccionada) {
                0 -> InventoryScreen(viewModel = viewModel)
                1 -> ProductoFormScreen(
                    onProductoGuardado = { producto ->
                        viewModel.agregarProducto(producto)
                        pestañaSeleccionada = 0
                    }
                )
                2 -> NotificacionesScreen()
                3 -> EscanerScreen()
            }
        }
    }
}