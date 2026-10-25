package com.duoc.kaijuinventory.ui.theme

import androidx.compose.ui.graphics.Color

// ---------- Paleta principal de Kaiju ----------
// Azul: color de marca (barras, botones principales)
val AzulKaiju = Color(0xFF1E4FA3)
val AzulKaijuOscuro = Color(0xFF0B2A63)
val AzulKaijuClaro = Color(0xFFDCE6FA)
val AzulKaijuPastel = Color(0xFFAFC6FF)

// Ámbar: color de acento (destacar precios y acciones secundarias)
val AmbarKaiju = Color(0xFFB26A00)
val AmbarKaijuClaro = Color(0xFFFFE2B8)
val AmbarKaijuOscuro = Color(0xFF3E2400)

// Neutros: fondos, tarjetas y bordes
val FondoClaro = Color(0xFFF4F6FB)
val SuperficieClara = Color(0xFFFFFFFF)
val SuperficieVariante = Color(0xFFE6EBF4)
val TextoSecundario = Color(0xFF4A5468)
val Borde = Color(0xFFC3CBD9)

val FondoOscuro = Color(0xFF10141C)
val SuperficieOscura = Color(0xFF181D27)
val SuperficieVarianteOscura = Color(0xFF2A3140)

// ---------- Colores de estado del stock ----------
// Se usan en inventario, movimientos y alertas para que signifiquen siempre lo mismo
val ColorExito = Color(0xFF2E7D32)       // stock disponible / entradas
val ColorExitoSuave = Color(0xFFE3F2E5)
val ColorAdvertencia = Color(0xFFE65100) // stock bajo / salidas
val ColorAdvertenciaSuave = Color(0xFFFFEEDD)
val RojoError = Color(0xFFC62828)        // sin stock / errores
val RojoErrorSuave = Color(0xFFFDE7E7)
