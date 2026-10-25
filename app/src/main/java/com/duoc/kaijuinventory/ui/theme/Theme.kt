package com.duoc.kaijuinventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ColoresClaros = lightColorScheme(
    primary = AzulKaiju,
    onPrimary = Color.White,
    primaryContainer = AzulKaijuClaro,
    onPrimaryContainer = AzulKaijuOscuro,
    secondary = AmbarKaiju,
    onSecondary = Color.White,
    secondaryContainer = AmbarKaijuClaro,
    onSecondaryContainer = AmbarKaijuOscuro,
    background = FondoClaro,
    onBackground = AzulKaijuOscuro,
    surface = SuperficieClara,
    onSurface = Color(0xFF1A1F2B),
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,
    surfaceContainer = SuperficieClara,
    outline = Borde,
    outlineVariant = SuperficieVariante,
    error = RojoError,
    errorContainer = RojoErrorSuave
)

private val ColoresOscuros = darkColorScheme(
    primary = AzulKaijuPastel,
    onPrimary = AzulKaijuOscuro,
    primaryContainer = Color(0xFF1C3A73),
    onPrimaryContainer = AzulKaijuClaro,
    secondary = Color(0xFFFFB95C),
    onSecondary = AmbarKaijuOscuro,
    secondaryContainer = Color(0xFF5A3600),
    onSecondaryContainer = AmbarKaijuClaro,
    background = FondoOscuro,
    onBackground = Color(0xFFE2E6EF),
    surface = SuperficieOscura,
    onSurface = Color(0xFFE2E6EF),
    surfaceVariant = SuperficieVarianteOscura,
    onSurfaceVariant = Color(0xFFBCC4D4),
    surfaceContainer = SuperficieOscura,
    outline = Color(0xFF5B6476),
    outlineVariant = SuperficieVarianteOscura,
    error = Color(0xFFEF9A9A),
    errorContainer = Color(0xFF5C1A1A)
)

// Bordes redondeados consistentes en tarjetas, botones y campos
private val FormasKaiju = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp)
)

// Tema de la app: todas las pantallas toman colores, tipografía y formas desde aquí
@Composable
fun KaijuInventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        typography = TipografiaKaiju,
        shapes = FormasKaiju,
        content = content
    )
}
