package com.duoc.kaijuinventory.data.remote

object ApiServices {

    val productos: ProductoApi by lazy {
        RetrofitInstance.retrofit.create(ProductoApi::class.java)
    }
}
