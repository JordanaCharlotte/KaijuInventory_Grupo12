package com.duoc.kaijuinventory.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Configuración de Retrofit para los microservicios propios.
object BackendCliente {

    // 127.0.0.1 funciona en el emulador gracias a "adb reverse", que redirige los puertos
    // 8081, 8082 y 8083 del emulador al PC donde corren los microservicios.
    // Antes de abrir la app (y cada vez que se reinicia el emulador) hay que ejecutar:
    //   adb reverse tcp:8081 tcp:8081
    //   adb reverse tcp:8082 tcp:8082
    //   adb reverse tcp:8083 tcp:8083
    // Sin adb reverse, en el emulador se puede usar "10.0.2.2".
    // Para un celular real, cambiar por la IP del PC en la red Wi-Fi (ejemplo: "192.168.1.15").
    const val IP_SERVIDOR = "127.0.0.1"

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
