package com.duoc.kaijuinventory.data.remote

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

// Convierte un error de red o del backend en un mensaje claro para el usuario
fun mensajeDeError(error: Exception): String = when (error) {
    is HttpException -> {
        // El backend responde {"mensaje": "..."}; si no se puede leer, se muestra el código
        val cuerpo = error.response()?.errorBody()?.string()
        val mensaje = try {
            Gson().fromJson(cuerpo, ErrorDto::class.java)?.mensaje
        } catch (e: Exception) {
            null
        }
        mensaje ?: "Error del servidor (${error.code()})"
    }
    is IOException -> "No se pudo conectar con el servidor. Revisa que los microservicios estén encendidos."
    else -> "Ocurrió un error inesperado"
}
