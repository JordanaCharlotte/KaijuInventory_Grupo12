package com.duoc.kaijuinventory.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

// Muestra la imagen del producto en este orden:
// 1) la foto que tomo o eligio el usuario
// 2) la imagen de referencia segun el tipo de producto
// 3) un icono por defecto
@Composable
fun ImagenInteligente(
    fotoPath: String,
    modifier: Modifier = Modifier,
    imagenReferencia: Int? = null,
    tamano: Dp = 120.dp,
    forma: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(forma)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (fotoPath.isNotBlank()) {
            AsyncImage(
                model = File(fotoPath),
                contentDescription = "Foto del producto",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (imagenReferencia != null) {
            Image(
                painter = painterResource(id = imagenReferencia),
                contentDescription = "Imagen de referencia del producto",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Producto sin foto",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(tamano / 2)
            )
        }
    }
}
