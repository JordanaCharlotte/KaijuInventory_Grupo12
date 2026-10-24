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
import com.duoc.kaijuinventory.ui.components.ImagenInteligente
import com.duoc.kaijuinventory.util.copiarImagenALocal
import com.duoc.kaijuinventory.util.crearArchivoFoto
import com.duoc.kaijuinventory.viewmodel.ProductoFormViewModel
import java.io.File

@Composable
fun ProductoFormScreen(
    viewModel: ProductoFormViewModel = viewModel(),
    onProductoGuardado: (Producto) -> Unit = {}
) {
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current

    // Ruta del archivo donde la cámara va a guardar la foto
    var rutaCamara by rememberSaveable { mutableStateOf<String?>(null) }

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
        if (tienePermiso) abrirCamara() else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Agregar Producto", style = MaterialTheme.typography.headlineSmall)

        // Foto del producto: galería o cámara
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ImagenInteligente(fotoPath = formState.fotoPath)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { galeriaLauncher.launch("image/*") }) {
                    Text("Galería")
                }
                OutlinedButton(onClick = { alPresionarCamara() }) {
                    Text("Cámara")
                }
            }
        }

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