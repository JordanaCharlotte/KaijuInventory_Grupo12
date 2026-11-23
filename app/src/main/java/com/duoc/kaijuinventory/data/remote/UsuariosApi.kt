package com.duoc.kaijuinventory.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

// Endpoints del microservicio ms-usuarios (puerto 8083)
interface UsuariosApi {

    @POST("api/auth/login")
    suspend fun login(@Body datos: LoginRequestDto): UsuarioDto
}
