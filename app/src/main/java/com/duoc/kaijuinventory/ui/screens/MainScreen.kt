package com.duoc.kaijuinventory.screens

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
import com.duoc.kaijuinventory.screens.NotificacionesScreen
import com.duoc.kaijuinventory.ui.screens.EscanerScreen
import com.duoc.kaijuinventory.ui.screens.InventoryScreen
import com.duoc.kaijuinventory.ui.screens.MovimientoScreen
import com.duoc.kaijuinventory.ui.screens.ProductoFormScreen
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel

private val pestañas = listOf(
    "📦" to "Inventario",
    "➕" to "Agregar",
    "🔄" to "Registro",
    "🔔" to "Alertas",
    "📷" to "Escáner"
)

@Composable
fun MainScreen(viewModel: InventoryViewModel) {
    // Guardamos cuál pestaña está seleccionada
    var pestañaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                pestañas.forEachIndexed { index, (icono, etiqueta) ->
                    NavigationBarItem(
                        selected = pestañaSeleccionada == index,
                        onClick = { pestañaSeleccionada = index },
                        icon = { Text(icono) },
                        label = { Text(etiqueta) }
                    )
                }
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
                2 -> MovimientoScreen(inventoryViewModel = viewModel)
                3 -> NotificacionesScreen()
                4 -> EscanerScreen()
            }
        }
    }
}