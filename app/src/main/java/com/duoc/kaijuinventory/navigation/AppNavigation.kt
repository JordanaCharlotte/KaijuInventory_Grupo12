package com.duoc.kaijuinventory.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.duoc.kaijuinventory.ui.screens.InventoryScreen
import com.duoc.kaijuinventory.ui.screens.MovimientoScreen
import com.duoc.kaijuinventory.ui.screens.NotificacionesScreen
import com.duoc.kaijuinventory.ui.screens.ProductoDetalleScreen
import com.duoc.kaijuinventory.ui.screens.ProductoFormScreen
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: InventoryViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route
    val productos by viewModel.productos.collectAsState()
    val enDetalle = rutaActual == Screen.Detalle.route

    // Para mostrar mensajes cortos abajo de la pantalla (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (enDetalle) "Detalle del producto" else "Kaiju Inventory") },
                // En el detalle aparece la flecha para volver al inventario
                navigationIcon = {
                    if (enDetalle) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Screen.items.forEach { pantalla ->
                    NavigationBarItem(
                        // Si estamos en el detalle, se marca la pestaña Inventario
                        selected = rutaActual == pantalla.route ||
                            (enDetalle && pantalla == Screen.Inventario),
                        onClick = {
                            // Desde el detalle, tocar Inventario solo vuelve atrás
                            if (enDetalle && pantalla == Screen.Inventario) {
                                navController.popBackStack()
                            } else {
                                navController.navegarA(pantalla.route)
                            }
                        },
                        icon = { Icon(pantalla.icono, contentDescription = pantalla.etiqueta) },
                        label = { Text(pantalla.etiqueta) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inventario.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Inventario.route) {
                InventoryScreen(
                    viewModel = viewModel,
                    onProductoClick = { id -> navController.navigate(Screen.Detalle.crearRuta(id)) }
                )
            }
            composable(
                route = Screen.Detalle.route,
                arguments = listOf(navArgument("productoId") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("productoId") ?: 0
                ProductoDetalleScreen(productoId = id, viewModel = viewModel)
            }
            composable(Screen.Agregar.route) {
                ProductoFormScreen(
                    productosExistentes = productos,
                    onProductoGuardado = { producto ->
                        viewModel.agregarProducto(producto)
                        navController.navegarA(Screen.Inventario.route)
                        scope.launch {
                            snackbarHostState.showSnackbar("Producto \"${producto.nombre}\" guardado")
                        }
                    }
                )
            }
            composable(Screen.Registro.route) {
                MovimientoScreen(inventoryViewModel = viewModel)
            }
            composable(Screen.Alertas.route) {
                NotificacionesScreen(viewModel = viewModel)
            }
        }
    }
}

// Navega a una pestaña sin apilar copias y conservando el estado de las demás
private fun NavHostController.navegarA(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}