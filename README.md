# Kaiju Inventory

App Android para la gestión y trazabilidad del inventario de **Kaiju**.
Desarrollo de Aplicaciones Móviles (DSY1105) · Duoc UC San Bernardo.

**Integrantes:** Jordana Vásquez y Davis Monsalvez (Grupo 12)

## Repositorios del proyecto

| Repositorio | Contenido |
|---|---|
| [KaijuInventory_Grupo12](https://github.com/JordanaCharlotte/KaijuInventory_Grupo12) | App Android (este repositorio) |
| [Kaiju_Backend](https://github.com/JordanaCharlotte/Kaiju_Backend) | Microservicios Spring Boot: ms-usuarios, ms-productos y ms-movimientos |

## De qué trata

Kaiju lleva su inventario a mano, y eso hace que el stock anotado casi nunca coincida con el real: se olvidan entradas o salidas y nadie se entera cuando un producto se está acabando. Con la app se puede ver el stock de cada producto, registrar lo que entra y lo que sale, y saber a tiempo qué hay que reponer.

Todos los datos que usa la app son ficticios, como exige el caso.

## Funcionalidades

- **Inicio de sesión** contra el microservicio `ms-usuarios`, con validación por campo, sesión recordada y botón para cerrar sesión.
- **Inventario** con resumen del stock (disponibles, stock bajo y sin stock), buscador que no distingue tildes y filtro por categoría.
- **Detalle de producto** con su stock, totales de entradas, salidas y ventas, e historial de movimientos (trazabilidad).
- **Agregar productos** con un formulario que valida cada campo (incluido el código de barra repetido) y se guarda en `ms-productos`.
- **Registrar entradas, salidas y ventas** en `ms-movimientos`, que valida el stock y lo actualiza en `ms-productos`.
- **Alertas** con los productos por reponer y **notificación** cuando un producto llega a su stock mínimo.
- **Foto del producto** con la cámara o la galería; si no tiene, muestra una imagen de referencia según el tipo de producto.
- **Sin conexión**: si los microservicios no responden, muestra la última copia guardada en el teléfono y un botón "Reintentar".
- **API externa Open Food Facts** para buscar datos de un producto por su código de barras.
- Tema propio con la paleta de Kaiju, modo oscuro y animaciones (transiciones, barras de stock, mensajes).

## Arquitectura

Kotlin + Jetpack Compose + Material 3, con el patrón **MVVM**. Las pantallas solo muestran el estado; la lógica y las validaciones están en los ViewModels.

```
Pantallas (ui/screens)  ──►  ViewModels (viewmodel)  ──►  Repositorios (repository)
                                                              │
                         ┌────────────────────────────────────┼──────────────────────┐
                         ▼                                    ▼                      ▼
              Microservicios propios                 API externa             Copia local
              (Retrofit, data/remote)             Open Food Facts           (DataStore)
              8083 usuarios · 8081 productos       (Retrofit, aparte)
              8082 movimientos
```

Carpetas dentro de `app/src/main/java/com/duoc/kaijuinventory/`:

| Carpeta | Contenido |
|---|---|
| `ui/screens` | Pantallas: login, inventario, detalle, agregar, registro y alertas |
| `ui/components` | Componentes reutilizables (campo de formulario, imagen, diálogo de permisos) |
| `ui/theme` | Colores, tipografía y formas de Kaiju |
| `viewmodel` | Estado de cada pantalla y validaciones |
| `repository` | Conecta los ViewModels con el backend, la API externa y DataStore |
| `data/remote` | Interfaces Retrofit, DTOs y configuración de los clientes HTTP |
| `data` | DataStore (copia local y sesión) |
| `model` | Clases de datos de la app |
| `navigation` | Rutas y navegación entre pantallas |
| `util` | Funciones de apoyo: validaciones, texto, imágenes y notificaciones |

## Cómo ejecutarlo

1. Encender los tres microservicios de [Kaiju_Backend](https://github.com/JordanaCharlotte/Kaiju_Backend) (ver su README).
2. Abrir este proyecto en Android Studio y esperar el Sync de Gradle.
3. Ejecutar en un emulador con Android 11 (API 30) o superior.
4. Iniciar sesión con un usuario de prueba:

| Correo | Contraseña | Rol |
|---|---|---|
| admin@kaiju.cl | admin123 | Administrador |
| vendedor@kaiju.cl | vendedor123 | Vendedor |
| inventario@kaiju.cl | inventario123 | Encargado de inventario |

La app se conecta a `127.0.0.1` (`IP_SERVIDOR` en `data/remote/BackendCliente.kt`). Para que el emulador llegue a los microservicios del PC, con el emulador encendido se ejecuta una vez (y de nuevo cada vez que se reinicia el emulador):

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb reverse tcp:8081 tcp:8081
& $adb reverse tcp:8082 tcp:8082
& $adb reverse tcp:8083 tcp:8083
```

Sin `adb reverse`, en el emulador se puede usar la IP `10.0.2.2`. Para usar un **celular real**, cambiar `IP_SERVIDOR` por la IP del PC en la red Wi-Fi (el celular y el PC deben estar en la misma red).

## Pruebas unitarias

```powershell
.\gradlew.bat testDebugUnitTest
```

| Prueba | Qué revisa |
|---|---|
| `ProductoExternoRepositoryTest` | Búsqueda en Open Food Facts con una API falsa: producto encontrado, no encontrado, código inválido, producto sin nombre y sin internet |
| `ValidacionesLoginTest` | Validaciones del correo y la contraseña del inicio de sesión |

## APK firmado

El APK de release se firma con el keystore `keystore/kaiju-release.jks` (alias `kaiju`, RSA 2048, validez 10.000 días).
Las contraseñas **no están en el repositorio**: se leen del archivo `keystore.properties`, que está en `.gitignore`.

1. Copiar `keystore.properties.example` como `keystore.properties` y completar las contraseñas.
2. Generar el APK:

```powershell
.\gradlew.bat assembleRelease
```

3. El APK queda en `app/build/outputs/apk/release/app-release.apk`.
4. Para verificar la firma: `apksigner verify --verbose app-release.apk` (debe indicar `Verified using v2 scheme: true`).

La configuración de la firma está en `app/build.gradle.kts`, dentro de `signingConfigs`.
