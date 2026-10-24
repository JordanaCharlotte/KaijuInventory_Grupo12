package com.duoc.kaijuinventory.navigation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.duoc.kaijuinventory.ui.screens.InventoryScreen
import com.duoc.kaijuinventory.ui.screens.MovimientoScreen
import com.duoc.kaijuinventory.ui.screens.NotificacionesScreen
import com.duoc.kaijuinventory.ui.screens.ProductoFormScreen
import com.duoc.kaijuinventory.util.tienePermisoNotificaciones
import com.duoc.kaijuinventory.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: InventoryViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // Al abrir la app se pide el permiso de notificaciones (Android 13 o superior)
    val permisoNotificaciones = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !tienePermisoNotificaciones(context)
        ) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kaiju Inventory") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                Screen.items.forEach { pantalla ->
                    NavigationBarItem(
                        selected = rutaActual == pantalla.route,
                        onClick = { navController.navegarA(pantalla.route) },
                        icon = { Icon(pantalla.icono, contentDescription = pantalla.etiqueta) },
                        label = { Text(pantalla.etiqueta) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inventario.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Inventario.route) {
                InventoryScreen(viewModel = viewModel)
            }
            composable(Screen.Agregar.route) {
                ProductoFormScreen(
                    onProductoGuardado = { producto ->
                        viewModel.agregarProducto(producto)
                        navController.navegarA(Screen.Inventario.route)
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