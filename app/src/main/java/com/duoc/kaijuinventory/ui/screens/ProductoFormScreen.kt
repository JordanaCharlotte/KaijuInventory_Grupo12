package com.duoc.kaijuinventory.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.kaijuinventory.model.Producto
import com.duoc.kaijuinventory.ui.components.CampoFormulario
import com.duoc.kaijuinventory.ui.components.DialogoPermiso
import com.duoc.kaijuinventory.ui.components.ImagenInteligente
import com.duoc.kaijuinventory.util.copiarImagenALocal
import com.duoc.kaijuinventory.util.crearArchivoFoto
import com.duoc.kaijuinventory.util.imagenDeReferencia
import com.duoc.kaijuinventory.viewmodel.ProductoFormViewModel
import java.io.File

@Composable
fun ProductoFormScreen(
    productosExistentes: List<Producto> = emptyList(),
    viewModel: ProductoFormViewModel = viewModel(),
    onProductoGuardado: (Producto) -> Unit = {}
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current

    // Ruta del archivo donde la cámara va a guardar la foto
    var rutaCamara by rememberSaveable { mutableStateOf<String?>(null) }
    var mostrarDialogoCamara by remember { mutableStateOf(false) }

    // Galería: el usuario elige una imagen que ya existe
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val ruta = copiarImagenALocal(context, uri)
            if (ruta != null) {
                viewModel.onFotoChange(ruta)
            } else {
                Toast.makeText(context, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Cámara: toma una foto nueva. Si el usuario cancela, se borra el archivo vacío
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        val ruta = rutaCamara
        if (ruta != null) {
            if (exito) viewModel.onFotoChange(ruta) else File(ruta).delete()
        }
    }

    fun abrirCamara() {
        val archivo = crearArchivoFoto(context)
        rutaCamara = archivo.absolutePath
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        camaraLauncher.launch(uri)
    }

    // Permiso de cámara en tiempo de ejecución
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            Toast.makeText(
                context,
                "Se necesita el permiso de cámara para tomar la foto",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun alPresionarCamara() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        // Si no hay permiso, primero se explica en español para qué se usa la cámara
        if (tienePermiso) abrirCamara() else mostrarDialogoCamara = true
    }

    if (mostrarDialogoCamara) {
        DialogoPermiso(
            icono = Icons.Default.Info,
            titulo = "Usar la cámara",
            mensaje = "Kaiju Inventory necesita la cámara solo para tomar la foto del producto. " +
                "La foto se guarda en el teléfono y no se comparte.",
            onAceptar = {
                mostrarDialogoCamara = false
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
            },
            onCancelar = { mostrarDialogoCamara = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(text = "Agregar producto", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Completa los datos para sumarlo al inventario",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Sección 1: foto del producto
        SeccionFormulario(titulo = "Foto del producto") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mientras no haya foto, se muestra la imagen de referencia según lo que se escribe
                ImagenInteligente(
                    fotoPath = formState.fotoPath,
                    imagenReferencia = imagenDeReferencia(formState.nombre, formState.categoria)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }) {
                        Text("Galería")
                    }
                    Button(onClick = { alPresionarCamara() }) {
                        Text("Tomar foto")
                    }
                }
            }
        }

        // Sección 2: datos generales
        SeccionFormulario(titulo = "Datos del producto") {
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
        }

        // Sección 3: datos de inventario
        SeccionFormulario(titulo = "Inventario") {
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
            Text(
                text = "Cuando el stock llegue al mínimo, la app enviará una alerta.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = {
                val codigos = productosExistentes.map { it.codigoBarra }.filter { it.isNotBlank() }
                viewModel.guardarProducto(codigos) { producto -> onProductoGuardado(producto) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar producto")
        }
    }
}

// Tarjeta con título para agrupar los campos del formulario
@Composable
private fun SeccionFormulario(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            contenido()
        }
    }
}
