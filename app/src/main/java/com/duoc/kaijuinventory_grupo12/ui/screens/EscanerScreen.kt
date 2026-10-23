package com.duoc.kaijuinventory_grupo12.screens


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscanerScreen() {
    val context = LocalContext.current
    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        tienePermiso = isGranted
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kaiju Inventory - Escáner") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (tienePermiso) {
                // Vista cuando el permiso está activo
                Text(
                    text = "📷 ¡Cámara lista para escanear!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.typography.titleLarge.color
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Apunta al código de barras del producto para registrar su entrada o salida en el inventario.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // Vista cuando falta pedir el permiso nativo
                Text(
                    text = "Este módulo requiere acceso a la cámara del dispositivo.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        launcher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Conceder Permiso de Cámara")
                }
            }
        }
    }
}