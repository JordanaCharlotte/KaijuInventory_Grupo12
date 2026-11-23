package com.duoc.kaijuinventory.data.remote

// Objetos que viajan en JSON entre la app y los microservicios (Kaiju_Backend)

// ms-productos: GET y POST /api/productos
data class ProductoDto(
    val id: Long?,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val stockMinimo: Int,
    val codigoBarra: String?
)

// ms-movimientos: respuesta de GET y POST /api/movimientos
data class MovimientoDto(
    val id: Long,
    val productoId: Long,
    val productoNombre: String,
    val tipo: String,          // "ENTRADA", "SALIDA" o "VENTA"
    val cantidad: Int,
    val fecha: String          // ejemplo: "2026-11-23T17:59:20.460875"
)

// ms-movimientos: cuerpo del POST /api/movimientos
data class MovimientoRequestDto(
    val productoId: Long,
    val tipo: String,
    val cantidad: Int
)

// Cuerpo de los errores del backend: {"estado": 409, "mensaje": "Stock insuficiente..."}
data class ErrorDto(
    val estado: Int?,
    val mensaje: String?
)

// ms-usuarios: cuerpo del POST /api/auth/login
data class LoginRequestDto(
    val email: String,
    val password: String
)

// ms-usuarios: respuesta del login (nunca trae la contraseña)
data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String
)
