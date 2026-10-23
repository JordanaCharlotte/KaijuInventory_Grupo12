package com.duoc.kaijuinventory_grupo12.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.kaijuinventory_grupo12.model.Producto
import com.duoc.kaijuinventory_grupo12.viewmodel.ProductoFormViewModel

@Composable
fun ProductoFormScreen(
    viewModel: ProductoFormViewModel = viewModel(),
    onProductoGuardado: (Producto) -> Unit = {}
) {
    val formState = viewModel.formState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Agregar Producto", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = formState.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
            label = { Text("Nombre") },
            isError = formState.nombreError != null,
            supportingText = { formState.nombreError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.categoria,
            onValueChange = { viewModel.onCategoriaChange(it) },
            label = { Text("Categoría") },
            isError = formState.categoriaError != null,
            supportingText = { formState.categoriaError?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.stock,
            onValueChange = { viewModel.onStockChange(it) },
            label = { Text("Stock") },
            isError = formState.stockError != null,
            supportingText = { formState.stockError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.precio,
            onValueChange = { viewModel.onPrecioChange(it) },
            label = { Text("Precio") },
            isError = formState.precioError != null,
            supportingText = { formState.precioError?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.codigoBarra,
            onValueChange = { viewModel.onCodigoBarraChange(it) },
            label = { Text("Código de barra (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.guardarProducto { producto ->
                    onProductoGuardado(producto)
                    viewModel.resetForm()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        if (formState.isSaved) {
            Text(
                text = "✅ Producto guardado correctamente",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}