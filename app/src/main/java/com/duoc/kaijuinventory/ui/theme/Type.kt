package com.duoc.kaijuinventory.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Parte de la tipografía por defecto de Material 3 y refuerza títulos y etiquetas
private val Base = Typography()

val TipografiaKaiju = Typography(
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = Base.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelMedium = Base.labelMedium.copy(fontWeight = FontWeight.Medium)
)
