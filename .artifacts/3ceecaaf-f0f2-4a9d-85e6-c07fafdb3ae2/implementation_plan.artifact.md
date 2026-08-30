# Refactor de Proyecto: Modo Tienda Único

El objetivo de este plan es eliminar las funcionalidades de cliente y repartidor, dejando la aplicación exclusivamente en modo tienda. La pantalla de inicio (`SplashScreen`) navegará directamente a la gestión de pedidos de la tienda.

## Cambios Propuestos

### Componente: Navegación y Splash

#### [MODIFY] [AppNavigation.kt](file:///Users/tayler/Desktop/PizzzaStore/composeApp/src/androidMain/kotlin/com/pizzza/pizzzaapp/component/AppNavigation.kt)
- Cambiar el destino final del `SplashScreen` de `RoleSelection` a `Orders`.
- Eliminar las rutas y composables relacionados con:
  - `RoleSelection`
  - `Login`, `Register`, `AddressSelection`
  - `ClientHome`, `OrderDetail`, `CartDetail`, `OrderSummary`
  - `DriverHome`
- Eliminar la inyección y uso de `CartViewModel` y `AuthViewModel`.

#### [MODIFY] [SplashScreen.kt](file:///Users/tayler/Desktop/PizzzaStore/composeApp/src/androidMain/kotlin/com/pizzza/pizzzaapp/ui/splash/SplashScreen.kt)
- Asegurar que la lógica de navegación al finalizar la carga apunte directamente a la tienda (a través del callback `onFinished`).

### Componente: ViewModels y DI

#### [MODIFY] [AppViewModel.kt](file:///Users/tayler/Desktop/PizzzaStore/composeApp/src/androidMain/kotlin/com/pizzza/pizzzaapp/ui/AppViewModel.kt)
- Eliminar métodos relacionados con el carrito y clientes: `addToCart`, `removeCartItem`, `updateCartItem`, `clearCart`.

#### [MODIFY] [ViewModelModule.kt](file:///Users/tayler/Desktop/PizzzaStore/composeApp/src/androidMain/kotlin/com/pizzza/pizzzaapp/di/ViewModelModule.kt)
- Eliminar la declaración de `CartViewModel` y `AuthViewModel`.

#### [MODIFY] [MainActivity.kt](file:///Users/tayler/Desktop/PizzzaStore/composeApp/src/androidMain/kotlin/com/pizzza/pizzzaapp/ui/MainActivity.kt)
- Eliminar la inyección de `CartViewModel` y `AuthViewModel`.

### Componente: Limpieza de Archivos

#### [DELETE] [RoleSelectionScreen.kt](file:///Users/tayler/Desktop/PizzzaStore/composeApp/src/androidMain/kotlin/com/pizzza/pizzzaapp/ui/roles/RoleSelectionScreen.kt)
#### [DELETE] Directorio `ui/client/`
#### [DELETE] Directorio `ui/driver/`
#### [DELETE] Directorio `ui/auth/`
#### [DELETE] [CartViewModel.kt](file:///Users/tayler/Desktop/PizzzaStore/composeApp/src/androidMain/kotlin/com/pizzza/pizzzaapp/ui/CartViewModel.kt)

## Plan de Verificación

### Verificación Manual
1. Ejecutar la aplicación.
2. Comprobar que el `SplashScreen` carga los productos y navega directamente a "Gestión de Pedidos".
3. Verificar que no hay rastro de las opciones de cliente o repartidor en la interfaz.
4. Asegurar que las funcionalidades de tienda (pedidos, productos, sucursales) sigan funcionando correctamente.
