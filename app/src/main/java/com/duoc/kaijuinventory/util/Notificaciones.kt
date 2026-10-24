package com.duoc.kaijuinventory.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.duoc.kaijuinventory.model.Producto

private const val CANAL_STOCK_BAJO = "stock_bajo_channel"

// En Android 13+ las notificaciones necesitan permiso en tiempo de ejecución
fun tienePermisoNotificaciones(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

fun notificarStockBajo(context: Context, producto: Producto) {
    if (!tienePermisoNotificaciones(context)) return

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(
        NotificationChannel(
            CANAL_STOCK_BAJO,
            "Alertas de stock bajo",
            NotificationManager.IMPORTANCE_HIGH
        )
    )

    val texto = if (producto.stock == 0) {
        "${producto.nombre} se quedó sin stock."
    } else {
        "${producto.nombre}: quedan ${producto.stock} unidades (mínimo ${producto.stockMinimo})."
    }

    val notificacion = NotificationCompat.Builder(context, CANAL_STOCK_BAJO)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle("Stock bajo")
        .setContentText(texto)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    // Un id por producto: cada producto tiene su propia alerta
    manager.notify(producto.id, notificacion)
}