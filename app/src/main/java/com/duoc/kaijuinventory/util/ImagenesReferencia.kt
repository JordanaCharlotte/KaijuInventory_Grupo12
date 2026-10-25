package com.duoc.kaijuinventory.util

import com.duoc.kaijuinventory.R

// Devuelve una imagen de referencia segun el nombre o la categoria del producto.
// Se muestra cuando el producto todavia no tiene una foto propia.
fun imagenDeReferencia(nombre: String, categoria: String): Int? {
    val nombreMin = normalizarTexto(nombre)
    val categoriaMin = normalizarTexto(categoria)

    return when {
        "espresso" in nombreMin -> R.drawable.img_espresso
        "latte" in nombreMin -> R.drawable.img_latte
        "cappuccino" in nombreMin || "capuchino" in nombreMin -> R.drawable.img_cappuccino
        "muffin" in nombreMin || "queque" in nombreMin -> R.drawable.img_muffin
        // si no coincide el nombre, se usa una imagen segun la categoria
        "bebida" in categoriaMin || "cafe" in nombreMin -> R.drawable.img_espresso
        "reposter" in categoriaMin -> R.drawable.img_muffin
        else -> null
    }
}
