# Kaiju Inventory

Aplicación Android para la gestión y trazabilidad del inventario de Kaiju.

Proyecto desarrollado para la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)** de Duoc UC, sede San Bernardo.

**Integrantes:** Jordana Vásquez y Davis Monsalvez  
**Grupo:** 12

## Repositorios del proyecto

- [Repositorio Android: KaijuInventory_Grupo12](https://github.com/JordanaCharlotte/KaijuInventory_Grupo12 )
- [Repositorio backend: Kaiju_Backend](https://github.com/JordanaCharlotte/Kaiju_Backend )

El repositorio Android contiene la aplicación móvil. El repositorio `Kaiju_Backend` contiene los tres microservicios Spring Boot utilizados por la aplicación.

## Descripción del proyecto

Kaiju administra su inventario de forma manual, lo que puede provocar diferencias entre el stock registrado y el stock real. También pueden olvidarse entradas, salidas o ventas, y no siempre se detecta a tiempo cuándo un producto necesita reposición.

Kaiju Inventory permite consultar el inventario, registrar productos, controlar entradas y salidas, registrar ventas y recibir alertas cuando el stock está bajo.

Los datos utilizados en el proyecto son ficticios.

## Funcionalidades principales

- Inicio de sesión mediante el microservicio `ms-usuarios`.
- Validación de correo y contraseña.
- Sesión recordada y opción para cerrar sesión.
- Visualización del inventario.
- Resumen de productos disponibles, con stock bajo y sin stock.
- Búsqueda de productos.
- Filtro por categoría.
- Visualización del detalle de cada producto.
- Registro del historial de movimientos.
- Agregar productos con formulario validado.
- Validación de nombre, categoría, precio, stock, stock mínimo y código de barras.
- Validación para evitar precios y cantidades excesivamente altos.
- Validación para evitar ventas superiores al stock disponible.
- Registro de entradas, salidas y ventas.
- Actualización del stock después de registrar movimientos.
- Alertas de productos con stock bajo.
- Notificación cuando un producto llega a su stock mínimo.
- Agregar fotografías mediante cámara o galería.
- Imagen de referencia cuando el producto no tiene fotografía.
- Persistencia local de los productos y movimientos mediante Room.
- Persistencia de la sesión mediante DataStore.
- Recuperación de la última información guardada cuando el backend no está disponible.
- Botón para reintentar la conexión con los microservicios.
- Consumo de la API externa Open Food Facts.
- Búsqueda de productos mediante código de barras.
- Carga automática del nombre del producto desde Open Food Facts.
- Tema visual propio de Kaiju.
- Modo oscuro, animaciones y componentes desarrollados con Material 3.

## Tecnologías utilizadas

- Kotlin.
- Jetpack Compose.
- Material 3.
- Arquitectura MVVM.
- ViewModel.
- StateFlow.
- Room.
- SQLite.
- DataStore.
- Retrofit.
- Gson.
- Spring Boot.
- Microservicios.
- H2.
- Open Food Facts.
- JUnit y pruebas unitarias.

## Arquitectura de la aplicación

La aplicación utiliza una arquitectura MVVM.

```text
Pantallas Compose
        |
        v
ViewModel
        |
        v
Repository
        |
        +--> Room / SQLite
        |
        +--> Retrofit / Microservicios Spring Boot
        |
        +--> Retrofit / Open Food Facts
        |
        +--> DataStore para la sesión
```

Las pantallas muestran el estado de la aplicación. Los ViewModels contienen el estado y las validaciones. Los repositorios se encargan de conectar la aplicación con Room, los microservicios propios y la API externa.

## Persistencia local con Room

La información principal del inventario se almacena localmente mediante Room sobre SQLite.

Room se utiliza para guardar:

- Productos.
- Movimientos.
- Stock actualizado.
- Historial de operaciones.

La aplicación utiliza:

- Entidades Room.
- DAO para consultar y modificar información.
- Una base de datos Room.
- Un repositorio que coordina la información local y remota.

DataStore se mantiene para guardar la sesión del usuario.

## Microservicios del backend

La aplicación se conecta a tres microservicios Spring Boot:

| Microservicio | Función | Puerto |
|---|---|---:|
| `ms-usuarios` | Login y usuarios | `8083` |
| `ms-productos` | Productos e inventario | `8081` |
| `ms-movimientos` | Entradas, salidas y ventas | `8082` |

El backend se encuentra en un repositorio separado:

[Repositorio Kaiju_Backend](https://github.com/JordanaCharlotte/Kaiju_Backend )

## Endpoints principales

### Microservicio de usuarios

```text
POST /api/auth/login
GET  /api/usuarios
POST /api/usuarios
```

### Microservicio de productos

```text
GET    /api/productos
GET    /api/productos/{id}
GET    /api/productos/stock-bajo
POST   /api/productos
PUT    /api/productos/{id}
DELETE /api/productos/{id}
PATCH  /api/productos/{id}/stock
```

### Microservicio de movimientos

```text
GET  /api/movimientos
GET  /api/movimientos/{id}
GET  /api/movimientos/producto/{productoId}
POST /api/movimientos
```

## Usuarios de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| `admin@kaiju.cl` | `admin123` | Administrador |
| `vendedor@kaiju.cl` | `vendedor123` | Vendedor |
| `inventario@kaiju.cl` | `inventario123` | Encargado de inventario |

## Cómo ejecutar el backend

Primero se debe clonar o descargar el repositorio:

```text
https://github.com/JordanaCharlotte/Kaiju_Backend
```

Luego se debe iniciar cada microservicio en una terminal separada.

### Ejecutar productos

```powershell
cd ms-productos
.\mvnw.cmd spring-boot:run
```

### Ejecutar movimientos

```powershell
cd ms-movimientos
.\mvnw.cmd spring-boot:run
```

### Ejecutar usuarios

```powershell
cd ms-usuarios
.\mvnw.cmd spring-boot:run
```

Los tres servicios deben quedar ejecutándose al mismo tiempo.

El backend utiliza Spring Boot y una base de datos H2.

## Cómo ejecutar la aplicación Android

1. Abrir el proyecto en Android Studio.
2. Esperar a que termine la sincronización de Gradle.
3. Encender los microservicios del backend.
4. Ejecutar la aplicación en un emulador o dispositivo físico.
5. Iniciar sesión con uno de los usuarios de prueba.

### Uso con emulador Android

Para el emulador se puede utilizar:

```kotlin
const val IP_SERVIDOR = "10.0.2.2"
```

El emulador utiliza `10.0.2.2` para acceder al computador donde se ejecuta el backend.

Otra alternativa es utilizar `127.0.0.1` junto con `adb reverse`:

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

& $adb reverse tcp:8081 tcp:8081
& $adb reverse tcp:8082 tcp:8082
& $adb reverse tcp:8083 tcp:8083
```

Estos comandos deben ejecutarse con el emulador encendido.

### Uso con un celular físico

El celular y el computador deben estar conectados a la misma red Wi-Fi.

En `BackendCliente.kt` se debe utilizar la dirección IPv4 local del computador:

```kotlin
const val IP_SERVIDOR = "192.168.1.13"
```

La dirección puede cambiar dependiendo de la red. Se puede consultar en Windows con:

```powershell
ipconfig
```

No se debe utilizar la IP pública de Internet. Se debe utilizar la IPv4 local del computador conectado a la misma red Wi-Fi que el teléfono.

## API externa Open Food Facts

La aplicación utiliza la API externa Open Food Facts para buscar información de productos mediante su código de barras.

La búsqueda se realiza desde la pantalla **Agregar producto**.

Como prueba se puede utilizar el siguiente código:

```text
3017620422003
```

Este código corresponde a Nutella y permite completar automáticamente el nombre del producto.

La aplicación también controla:

- Código de barras inválido.
- Producto no encontrado.
- Falta de conexión a Internet.
- Producto sin nombre.

La API externa utilizada es:

```text
https://world.openfoodfacts.org
```

## Pruebas unitarias Android

Para ejecutar las pruebas unitarias Android:

```powershell
.\gradlew.bat testDebugUnitTest
```

Las pruebas verifican principalmente:

- Validaciones del inicio de sesión.
- Validación de códigos de barras.
- Búsqueda de productos en Open Food Facts.
- Producto encontrado.
- Producto no encontrado.
- Código inválido.
- Producto sin nombre.
- Error por falta de conexión.

El resultado esperado es:

```text
BUILD SUCCESSFUL
```

## Pruebas del backend

Desde la carpeta de cada microservicio se pueden ejecutar sus pruebas con:

```powershell
.\mvnw.cmd test
```

Ejemplo:

```powershell
cd ms-productos
.\mvnw.cmd test
```

También se debe ejecutar para:

```text
ms-usuarios
ms-movimientos
```

## APK firmado de release

La aplicación cuenta con una configuración para generar un APK firmado en modo release.

El archivo de configuración privado es:

```text
keystore.properties
```

Ese archivo contiene las contraseñas de firma y no se sube al repositorio.

La plantilla disponible en el proyecto es:

```text
keystore.properties.example
```

Para generar el APK release:

```powershell
.\gradlew.bat assembleRelease
```

El APK generado queda en:

```text
app/build/outputs/apk/release/app-release.apk
```

El APK release puede instalarse en un emulador o en un teléfono Android.

Para instalarlo mediante ADB:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r ".\app\build\outputs\apk\release\app-release.apk"
```

## Archivos privados

Por seguridad, los siguientes archivos no deben subirse a GitHub:

```text
keystore.properties
keystore/*.jks
```

El archivo que sí se puede mantener en el repositorio es:

```text
keystore.properties.example
```

Este archivo solo contiene la estructura de configuración y no contiene las contraseñas reales.

## Estructura principal del proyecto

```text
app/src/main/java/com/duoc/kaijuinventory/

├── data
│   ├── local
│   │   ├── Entidades Room
│   │   ├── DAO
│   │   └── Base de datos
│   └── remote
│       ├── APIs Retrofit
│       ├── DTOs
│       └── Configuración de clientes
├── model
├── navigation
├── repository
├── ui
│   ├── components
│   ├── screens
│   └── theme
├── util
└── viewmodel
```

## Estado del proyecto

La aplicación fue probada en:

- Login.
- Inventario.
- Agregar productos.
- Validaciones de formularios.
- Búsqueda en Open Food Facts.
- Registro de ventas.
- Bloqueo de ventas superiores al stock.
- Actualización del stock.
- Notificaciones de stock bajo.
- Persistencia al cerrar y abrir la aplicación.
- Conexión con los tres microservicios.
- APK release firmado instalado en un dispositivo Android.
