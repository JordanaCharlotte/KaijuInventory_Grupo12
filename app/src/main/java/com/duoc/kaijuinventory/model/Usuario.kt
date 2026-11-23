package com.duoc.kaijuinventory.model

// Usuario que inició sesión (datos que entrega ms-usuarios, sin la contraseña)
data class Usuario(
    val nombre: String,
    val email: String,
    val rol: String   // "ADMINISTRADOR", "VENDEDOR" o "ENCARGADO_INVENTARIO"
) {
    // "ENCARGADO_INVENTARIO" -> "Encargado inventario"
    val rolLegible: String
        get() = rol.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}
