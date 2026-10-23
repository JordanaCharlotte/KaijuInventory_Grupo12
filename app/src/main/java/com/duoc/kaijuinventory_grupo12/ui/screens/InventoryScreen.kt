package com.duoc.kaijuinventory_grupo12.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duoc.kaijuinventory_grupo12.model.Producto
import com.duoc.kaijuinventory_grupo12.viewmodel.InventoryViewModel

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val productos by viewModel.productos.collectAsState()

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
            items(productos) { producto ->
                ProductoItem(producto = producto)
            }
        }
    }
}

@Composable
fun ProductoItem(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Categoría: ${producto.categoria}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Precio: $${producto.precio}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(4.dp))

            if (producto.stock == 0) {
                Text(
                    text = "⚠️ ¡SIN STOCK!",
                    color = Color.Red,
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Text(
                    text = "Stock disponible: ${producto.stock}",
                    color = Color(0xFF008000),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}