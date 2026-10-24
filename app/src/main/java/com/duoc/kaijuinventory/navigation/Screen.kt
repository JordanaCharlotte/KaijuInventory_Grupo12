package com.duoc.kaijuinventory.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.vector.ImageVector

// Rutas de la app definidas de forma segura con una sealed class
sealed class Screen(val route: String, val etiqueta: String, val icono: ImageVector) {

    data object Inventario : Screen("inventario_page", "Inventario", Icons.Default.Home)

    data object Agregar : Screen("agregar_page", "Agregar", Icons.Default.Add)

    data object Registro : Screen("registro_page", "Registro", Icons.Default.Refresh)

    data object Alertas : Screen("alertas_page", "Alertas", Icons.Default.Notifications)

    companion object {
        // Destinos que aparecen en la barra de navegación inferior
        val items: List<Screen> = listOf(Inventario, Agregar, Registro, Alertas)
    }
}