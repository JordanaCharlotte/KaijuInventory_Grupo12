package com.duoc.kaijuinventory.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Configuración de Retrofit para los microservicios propios.
object BackendCliente {

    // 10.0.2.2 es la dirección con la que el EMULADOR llega al PC donde corren los microservicios.
    // Para un celular real, cambiar por la IP del PC en la red Wi-Fi (ejemplo: "192.168.1.15").
    const val IP_SERVIDOR = "10.0.2.2"

    // Si el backend está apagado, se espera máximo 5 segundos en vez de 10
    private val clienteHttp = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private fun crearRetrofit(urlBase: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(urlBase)
            .client(clienteHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val productosApi: ProductosApi by lazy {
        crearRetrofit("http://$IP_SERVIDOR:8081/").create(ProductosApi::class.java)
    }

    val movimientosApi: MovimientosApi by lazy {
        crearRetrofit("http://$IP_SERVIDOR:8082/").create(MovimientosApi::class.java)
    }

    val usuariosApi: UsuariosApi by lazy {
        crearRetrofit("http://$IP_SERVIDOR:8083/").create(UsuariosApi::class.java)
    }
}
