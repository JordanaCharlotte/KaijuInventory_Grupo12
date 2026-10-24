package com.duoc.kaijuinventory.ui.components

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

// Muestra la foto del producto en círculo; si no hay foto, muestra un ícono por defecto
@Composable
fun ImagenInteligente(
    fotoPath: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 120.dp
) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
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