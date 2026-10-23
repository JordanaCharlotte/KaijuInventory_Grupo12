package com.duoc.kaijuinventory_grupo12.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

@Composable
fun NotificacionesScreen() {
    val context = LocalContext.current
    var permisoConcedido by remember { mutableStateOf(tienePermisoNotificaciones(context)) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> permisoConcedido = isGranted }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Notificaciones", style = MaterialTheme.typography.headlineSmall)

        Text(
            text = if (permisoConcedido) "✅ Permiso de notificaciones concedido"
            else "⚠️ Permiso de notificaciones no concedido"
        )

        if (!permisoConcedido && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Button(onClick = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                Text("Solicitar permiso")
            }
        }

        Button(
            onClick = { enviarNotificacionStockBajo(context) },
            enabled = permisoConcedido || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            Text("Probar alerta de stock bajo")
        }
    }
}

private fun tienePermisoNotificaciones(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}

private fun enviarNotificacionStockBajo(context: Context) {
    val channelId = "stock_bajo_channel"
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId, "Alertas de Stock Bajo", NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("⚠️ Stock bajo")
        .setContentText("Un producto está por debajo del stock mínimo.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        notificationManager.notify(1001, builder.build())
    }
}