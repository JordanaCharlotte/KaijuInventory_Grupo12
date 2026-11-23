package com.duoc.kaijuinventory.util

// Validaciones del formulario de inicio de sesión.
// Devuelven el mensaje de error, o null si el dato está bien.

fun validarCorreo(email: String): String? = when {
    email.isBlank() -> "El correo es obligatorio"
    !email.trim().matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) -> "Ingresa un correo válido (ejemplo: nombre@kaiju.cl)"
    else -> null
}

fun validarPassword(password: String): String? = when {
    password.isBlank() -> "La contraseña es obligatoria"
    password.length < 6 -> "Debe tener al menos 6 caracteres"
    else -> null
}
