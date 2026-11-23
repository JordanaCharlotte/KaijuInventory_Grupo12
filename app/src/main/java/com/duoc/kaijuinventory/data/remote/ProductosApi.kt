package com.duoc.kaijuinventory.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Endpoints del microservicio ms-productos (puerto 8081)
interface ProductosApi {

    @GET("api/productos")
    suspend fun listar(): List<ProductoDto>

    @GET("api/productos/{id}")
    suspend fun buscar(@Path("id") id: Long): ProductoDto

    @POST("api/productos")
    suspend fun crear(@Body producto: ProductoDto): ProductoDto
}
