package com.duoc.kaijuinventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresClaros = lightColorScheme(
    primary = AzulKaiju,
    onPrimary = Color.White,
    primaryContainer = AzulKaijuClaro,
    onPrimaryContainer = AzulKaijuOscuro,
    error = RojoError
)

private val ColoresOscuros = darkColorScheme(
    primary = AzulKaijuPastel,
    onPrimary = AzulKaijuOscuro,
    primaryContainer = AzulKaijuOscuro,
    onPrimaryContainer = AzulKaijuClaro,
    error = Color(0xFFEF9A9A)
)

// Tema de la app: todas las pantallas toman sus colores desde aquí
@Composable
fun KaijuInventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        content = content
    )
}