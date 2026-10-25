package com.duoc.kaijuinventory.util

import java.text.Normalizer

// Quita tildes y pasa a minúscula para comparar textos.
// Así "cafe", "Café" y "CAFÉ" se consideran iguales al buscar.
fun normalizarTexto(texto: String): String {
    val sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}"), "") // \p{Mn} son las marcas de tilde que quedan separadas
    return sinTildes.lowercase().trim()
}
