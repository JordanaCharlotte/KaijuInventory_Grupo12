package com.duoc.kaijuinventory.util

import android.content.Context
import android.net.Uri
import java.io.File

// Crea un archivo vacío dentro del almacenamiento privado de la app (carpeta "fotos")
fun crearArchivoFoto(context: Context): File {
    val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
    return File(carpeta, "producto_${System.currentTimeMillis()}.jpg")
}

// Copia la imagen elegida en la galería al almacenamiento de la app,
// así sigue disponible aunque se cierre la app. Devuelve la ruta o null si falla.
fun copiarImagenALocal(context: Context, uri: Uri): String? {
    return try {
        val destino = crearArchivoFoto(context)
        val entrada = context.contentResolver.openInputStream(uri) ?: return null
        entrada.use { input ->
            destino.outputStream().use { salida -> input.copyTo(salida) }
        }
        destino.absolutePath
    } catch (e: Exception) {
        null
    }
}