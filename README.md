# Kaiju Inventory

App Android para controlar el inventario de Kaiju. 
Evaluación Parcial 2 de Desarrollo de Aplicaciones Móviles (DSY1105).

Integrantes: Davis Monsalvez y Jordana Vásquez
(Grupo 12)

# De qué trata

Kaiju lleva su inventario a mano, y eso hace que el stock anotado casi nunca coincida con el real: se olvidan entradas o salidas y nadie se entera cuando un producto se está acabando. Con la app se puede ver el stock de cada producto, registrar lo que entra y lo que sale, y ver qué productos están bajos de stock.

Todos los datos que usa la app son inventados, solo para el trabajo.

Qué hace hasta ahora
Muestra el inventario con categoría, precio y el estado del stock (disponible, bajo o sin stock).
Permite agregar productos con un formulario que valida cada campo y avisa con ícono y mensaje cuando algo está mal.
Permite registrar entradas, salidas y ventas. No deja sacar más de lo que hay en stock, actualiza el stock y guarda un historial con la fecha.
Guarda todo en el teléfono con DataStore, así que los datos siguen ahí al cerrar la app.
Permite agregar una foto a cada producto con la cámara o desde la galería; si no tiene foto, muestra una imagen de referencia según el tipo de producto.
Envía una notificación cuando un producto llega a su stock mínimo y tiene una pantalla de alertas con los productos por reponer.
Usa un tema propio (ui/theme) con la paleta de colores de Kaiju y modo oscuro.
Cómo lo armamos

Está hecha en Kotlin con Jetpack Compose y Material 3, usando MVVM. Las validaciones están en los ViewModels y no en las pantallas. Las carpetas dentro de app/src/main/java/com/duoc/kaijuinventory/ son:

data: el acceso a DataStore
model: las clases de datos
repository: lo que conecta los datos con los ViewModels
viewmodel: la lógica y las validaciones
ui: las pantallas (screens), los componentes reutilizables (components) y el tema (theme)
util: funciones de apoyo para la cámara, imágenes y notificaciones


Para correrlo hay que clonar el repo:
git clone https://github.com/JordanaCharlotte/KaijuInventory_Grupo12.git
Abrirlo con Android Studio y esperar que termine el Sync de Gradle.
Ejecutarlo en un emulador o celular con Android 11 (API 30) o superior.
