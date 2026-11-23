package com.duoc.kaijuinventory.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Endpoints del microservicio ms-movimientos (puerto 8082)
interface MovimientosApi {

    @GET("api/movimientos")
    suspend fun listar(): List<MovimientoDto>

    @POST("api/movimientos")
    suspend fun registrar(@Body movimiento: MovimientoRequestDto): MovimientoDto
}
