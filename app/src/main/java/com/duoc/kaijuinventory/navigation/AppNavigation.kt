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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.kaijuinventory.ui.screens.InventoryScreen
import com.duoc.kaijuinventory.ui.screens.LoginScreen
import com.duoc.kaijuinventory.ui.screens.SplashScreen
import com.duoc.kaijuinventory.viewmodel.LoginViewModel
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
    val enLogin = rutaActual == Screen.Login.route

    // Sesión: se lee de DataStore al abrir la app
    val loginViewModel: LoginViewModel = viewModel()
    val sesionCargada by loginViewModel.sesionCargada.collectAsState()
    val usuario by loginViewModel.usuario.collectAsState()

    // Mientras se revisa si hay una sesión guardada, se muestra la pantalla de bienvenida
    if (!sesionCargada) {
        SplashScreen()
        return
    }

    // Si ya había sesión se entra directo al inventario; si no, al login.
    // Se calcula una sola vez para que la navegación no se reinicie.
    val destinoInicial = remember {
        if (usuario != null) Screen.Inventario.route else Screen.Login.route
    }

    // Para mostrar mensajes cortos abajo de la pantalla (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            // En la pantalla de login no se muestran la barra superior ni la inferior
            if (!enLogin) TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Círculo con las iniciales del usuario (no se muestra en el detalle, donde va la flecha)
                        usuario?.let {
                            if (!enDetalle) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.22f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it.iniciales,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                        Column {
                            Text(if (enDetalle) "Detalle del producto" else "Kaiju Inventory")
                            usuario?.let {
                                Text(
                                    text = "${it.rolLegible} · ${it.email}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        loginViewModel.cerrarSesion {
                            // Vuelve al login borrando todas las pantallas anteriores
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
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
            if (!enLogin) NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Screen.items.forEach { pantalla ->
                    NavigationBarItem(
                        // Si estamos en el detalle, se marca la pestaña Inventario
                        selected = rutaActual == pantalla.route ||
                            (enDetalle && pantalla == Screen.Inventario),
                        onClick = {
                            // Inventario es la pantalla de inicio: se vuelve a ella cerrando
                            // lo que esté encima (por ejemplo un detalle abierto)
                            if (pantalla == Screen.Inventario) {
                                navController.popBackStack(Screen.Inventario.route, inclusive = false)
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
            startDestination = destinoInicial,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = loginViewModel,
                    alIngresar = {
                        // Entra al inventario y saca el login de la pila (el botón atrás no vuelve al login)
                        navController.navigate(Screen.Inventario.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Inventario.route) {
                InventoryScreen(
                    viewModel = viewModel,
                    nombreUsuario = usuario?.nombre ?: "",
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
                        // Se envía a ms-productos y se espera la respuesta del servidor
                        viewModel.agregarProducto(producto) { error ->
                            if (error == null) {
                                navController.popBackStack(Screen.Inventario.route, inclusive = false)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Producto \"${producto.nombre}\" guardado en el servidor")
                                }
                            } else {
                                scope.launch { snackbarHostState.showSnackbar(error) }
                            }
                        }
                    }
                )
            }
            composable(Screen.Registro.route) {
                MovimientoScreen(inventoryViewModel = viewModel)
            }
            composable(Screen.Alertas.route) {
                NotificacionesScreen(
                    viewModel = viewModel,
                    onProductoClick = { id -> navController.navigate(Screen.Detalle.crearRuta(id)) }
                )
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