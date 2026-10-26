# Kaiju Inventory

App Android para controlar el inventario de Kaiju. La hicimos para la Evaluación Parcial 2 de Desarrollo de Aplicaciones Móviles (DSY1105) en Duoc UC.

Integrantes (Grupo 12): Davis Monsalvez y Jordana Vásquez

## De qué trata

Kaiju lleva su inventario a mano, y eso hace que el stock anotado casi nunca coincida con el real: se olvidan entradas o salidas y nadie se entera cuando un producto se está acabando. Con la app se puede ver el stock de cada producto, registrar lo que entra y lo que sale, y ver qué productos están bajos de stock.

Todos los datos que usa la app son inventados, solo para el trabajo.

## Qué hace

- Muestra el inventario con foto, categoría, precio y el estado del stock (disponible, bajo o sin stock).
- Permite agregar productos con un formulario que valida cada campo y avisa con ícono y mensaje cuando algo está mal.
- Cada producto puede tener una foto, que se saca con la cámara o se elige de la galería.
- Permite registrar entradas, salidas y ventas. No deja sacar más de lo que hay en stock, actualiza el stock y guarda un historial con la fecha.
- Cuando un movimiento deja un producto en su stock mínimo o por debajo, la app manda una notificación.
- La pestaña de alertas lista los productos que están bajos de stock.
- Guarda todo en el teléfono con DataStore, así que los datos siguen ahí al cerrar la app.
- Tiene animaciones al cambiar de pantalla, al cargar el inventario y al registrar un movimiento.

## Recursos nativos

- **Cámara y galería:** para la foto del producto. La app pide el permiso de cámara antes de abrirla y guarda la foto en la carpeta privada de la app usando un FileProvider. Si se cancela, no pasa nada.
- **Notificaciones:** avisan cuando el stock de un producto llega al mínimo. La app pide el permiso al abrirse.

## Cómo lo armamos

Está hecha en Kotlin con Jetpack Compose y Material 3, usando MVVM. Las validaciones están en los ViewModels y no en las pantallas. Las carpetas dentro de `app/src/main/java/com/duoc/kaijuinventory/` son:

- `data`: el acceso a DataStore
- `model`: las clases de datos
- `repository`: lo que conecta los datos con los ViewModels
- `viewmodel`: la lógica y las validaciones
- `navigation`: las rutas y el NavHost
- `ui`: las pantallas y los componentes reutilizables
- `util`: ayudas para las notificaciones y las fotos

## Cómo correrla

1. Clonar el repo: `git clone https://github.com/JordanaCharlotte/KaijuInventory_Grupo12.git`
2. Abrirlo con Android Studio y esperar que termine el Sync de Gradle.
3. Ejecutarlo en un emulador o celular con Android 11 (API 30) o superior.

