package com.duoc.kaijuinventory.model

data class LoginFormState(
    val email: String = "",
    val password: String = "",

    val emailError: String? = null,
    val passwordError: String? = null,

    // Error que responde el servidor (por ejemplo, contraseña incorrecta o sin conexión)
    val errorServidor: String? = null,
    val cargando: Boolean = false
)
