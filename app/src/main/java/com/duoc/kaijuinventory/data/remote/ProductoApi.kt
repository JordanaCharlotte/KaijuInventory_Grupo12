package com.duoc.kaijuinventory.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductoApi {

    @GET("productos")
    suspend fun obtenerProductos(): Response<List<ProductoDto>>

    @GET("productos/{id}")
    suspend fun obtenerProducto(
        @Path("id") id: Int
    ): Response<ProductoDto>

    @POST("productos")
    suspend fun crearProducto(
        @Body producto: ProductoDto
    ): Response<ProductoDto>

    @PUT("productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Int,
        @Body producto: ProductoDto
    ): Response<ProductoDto>

    @DELETE("productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Int
    ): Response<Unit>
}
