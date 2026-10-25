package com.duoc.kaijuinventory.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

// Explica en español para qué se necesita un permiso, antes de que Android lo pida.
// El cuadro de Android sale en el idioma del teléfono, por eso primero mostramos este.
@Composable
fun DialogoPermiso(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    onAceptar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(icono, contentDescription = null) },
        title = { Text(titulo) },
        text = { Text(mensaje) },
        confirmButton = {
            Button(onClick = onAceptar) { Text("Continuar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Ahora no") }
        }
    )
}
