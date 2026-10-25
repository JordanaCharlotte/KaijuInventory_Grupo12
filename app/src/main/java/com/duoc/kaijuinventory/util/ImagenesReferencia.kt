package com.duoc.kaijuinventory.util

import com.duoc.kaijuinventory.R

// Devuelve una imagen de referencia segun el nombre o la categoria del producto.
// Se muestra cuando el producto todavia no tiene una foto propia.
fun imagenDeReferencia(nombre: String, categoria: String): Int? {
    val nombreMin = normalizarTexto(nombre)
    val categoriaMin = normalizarTexto(categoria)

    return when {
        // primero se busca por el nombre del producto
        // (para "te" se buscan espacios alrededor, asi "chocolate" no cuenta como te)
        "espresso" in nombreMin -> R.drawable.img_espresso
        "latte" in nombreMin || "helado" in nombreMin -> R.drawable.img_latte
        "cappuccino" in nombreMin || "capuchino" in nombreMin || "chocolate caliente" in nombreMin -> R.drawable.img_cappuccino
        " te " in " $nombreMin " || "infusion" in nombreMin -> R.drawable.img_te
        "jugo" in nombreMin || "limonada" in nombreMin -> R.drawable.img_jugo
        "muffin" in nombreMin || "queque" in nombreMin -> R.drawable.img_muffin
        "croissant" in nombreMin || "medialuna" in nombreMin -> R.drawable.img_croissant
        "galleta" in nombreMin || "cookie" in nombreMin -> R.drawable.img_galleta
        "sandwich" in nombreMin || "sanguche" in nombreMin -> R.drawable.img_sandwich
        // si no coincide el nombre, se usa una imagen segun la categoria
        "fria" in categoriaMin -> R.drawable.img_jugo
        "bebida" in categoriaMin || "cafe" in nombreMin -> R.drawable.img_espresso
        "reposter" in categoriaMin -> R.drawable.img_muffin
        "salado" in categoriaMin -> R.drawable.img_sandwich
        else -> null
    }
}
