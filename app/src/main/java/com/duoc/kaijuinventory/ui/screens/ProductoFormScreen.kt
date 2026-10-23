package com.duoc.kaijuinventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.ui.components.CampoFormulario
import com.duoc.kaijuinventory.viewmodel.ProductoFormViewModel

@Composable
fun ProductoFormScreen(
    viewModel: ProductoFormViewModel = viewModel(),
    onProductoGuardado: (Producto) -> Unit = {}
) {
    val formState by viewModel.formState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Agregar Producto", style = MaterialTheme.typography.headlineSmall)

        CampoFormulario(
            value = formState.nombre,
            onValueChange = viewModel::onNombreChange,
            label = "Nombre",
            icono = Icons.Default.Edit,
            error = formState.nombreError
        )

        CampoFormulario(
            value = formState.categoria,
            onValueChange = viewModel::onCategoriaChange,
            label = "Categoría",
            icono = Icons.Default.Menu,
            error = formState.categoriaError
        )

        CampoFormulario(
            value = formState.stock,
            onValueChange = viewModel::onStockChange,
            label = "Stock inicial",
            icono = Icons.Default.Add,
            error = formState.stockError,
            keyboardType = KeyboardType.Number
        )

        CampoFormulario(
            value = formState.stockMinimo,
            onValueChange = viewModel::onStockMinimoChange,
            label = "Stock mínimo",
            icono = Icons.Default.Warning,
            error = formState.stockMinimoError,
            keyboardType = KeyboardType.Number
        )

        CampoFormulario(
            value = formState.precio,
            onValueChange = viewModel::onPrecioChange,
            label = "Precio",
            icono = Icons.Default.ShoppingCart,
            error = formState.precioError,
            keyboardType = KeyboardType.Decimal
        )

        CampoFormulario(
            value = formState.codigoBarra,
            onValueChange = viewModel::onCodigoBarraChange,
            label = "Código de barra (opcional)",
            icono = Icons.Default.Search,
            error = formState.codigoBarraError,
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = { viewModel.guardarProducto { producto -> onProductoGuardado(producto) } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar Producto")
        }
    }
}