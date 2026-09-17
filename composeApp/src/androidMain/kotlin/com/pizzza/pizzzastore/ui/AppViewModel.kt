package com.pizzza.pizzzastore.ui

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.printer.BluetoothPrinterManager
import com.pizzza.pizzzastore.printer.TicketFormatter
import com.pizzza.pizzzastore.repository.network.WebSocketManager
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.base.GlobalUiStateManager
import com.pizzza.pizzzastore.ui.orders.OrderUiState
import com.pizzza.pizzzastore.usecases.DataUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(
    private val dataUseCase: DataUseCase,
    private val printerManager: BluetoothPrinterManager,
    private val webSocketManager: WebSocketManager,
    private val globalUiStateManager: GlobalUiStateManager,
    private val prefs: SharedPreferences
) : BaseViewModel() {

    var orderUiState by mutableStateOf(OrderUiState())
        private set

    init {
        // Mover la detección de impresora a un hilo secundario para evitar ANRs en el arranque
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            printerManager.autoDetectAndConnect()
        }
        
        viewModelScope.launch {
            printerManager.isConnected.collectLatest { connected ->
                orderUiState = orderUiState.copy(isPrinterConnected = connected)
            }
        }

        viewModelScope.launch {
            webSocketManager.isConnected.collectLatest { connected ->
                orderUiState = orderUiState.copy(isSocketConnected = connected)
            }
        }

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val user = dataUseCase.getUserLocal()
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                orderUiState = orderUiState.copy(
                    userRole = user?.rol,
                    userArea = user?.area,
                    selectedBranchId = user?.area ?: orderUiState.selectedBranchId
                )
            }
            Log.d("AppViewModel", "🍕 Perfil cargado - Rol: ${user?.rol}, Sucursal: ${user?.area}")
        }
    }

    private var isFetchingOrders = false

    fun getGeneralOrderList() {
        if (orderUiState.isInitialLoaded || isFetchingOrders) return
        
        // Verificación de seguridad adicional: no llamar a la API si no hay sesión iniciada en la app
        if (orderUiState.userRole == null) {
            viewModelScope.launch {
                val user = dataUseCase.getUserLocal()
                if (user == null) {
                    Log.d("AppViewModel", "getGeneralOrderList omitido: Sin sesión en base de datos local")
                    return@launch
                } else {
                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                        orderUiState = orderUiState.copy(
                            userRole = user.rol,
                            userArea = user.area
                        )
                    }
                    proceedToLoadOrders()
                }
            }
        } else {
            proceedToLoadOrders()
        }
    }

    private fun proceedToLoadOrders() {
        if (isFetchingOrders) return
        isFetchingOrders = true
        
        Log.d("AppViewModel", "getGeneralOrderList: Iniciando ejecución")
        execute(globalUiStateManager = globalUiStateManager) {
            try {
                // Aseguramos tener el área más fresca posible
                val currentArea = if (orderUiState.userArea.isNullOrBlank()) {
                    io { dataUseCase.getUserLocal()?.area }
                } else {
                    orderUiState.userArea
                }

                // 1. Cargar pedidos usando el nuevo servicio por sucursal si el area está disponible
                val response = if (!currentArea.isNullOrBlank() && currentArea != "0") {
                    Log.d("AppViewModel", "Llamando servicio por sucursal: $currentArea")
                    dataUseCase.loadParentOrderByBranch(currentArea)
                } else {
                    Log.d("AppViewModel", "Llamando servicio general (Area null o 0)")
                    dataUseCase.loadParentOrder()
                }
                
                // 2. Cargar y filtrar conductores usando el nuevo servicio por sucursal
                if (orderUiState.drivers.isEmpty()) {
                    val localUser = io { dataUseCase.getUserLocal() }
                    val currentAreaForDrivers = localUser?.area ?: orderUiState.userArea
                    
                    if (!currentAreaForDrivers.isNullOrBlank() && currentAreaForDrivers != "0") {
                        val driversList = try { 
                            io { dataUseCase.getUsersByBranch(currentAreaForDrivers) } 
                        } catch (e: Exception) { 
                            emptyList() 
                        }
                        
                        withContext(kotlinx.coroutines.Dispatchers.Main) {
                            orderUiState = orderUiState.copy(drivers = driversList)
                        }
                        Log.d("AppViewModel", "🍕 Conductores cargados desde servicio de sucursal $currentAreaForDrivers: ${driversList.size}")
                    }
                }

                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    updateStateWithOrders(response)
                    orderUiState = orderUiState.copy(isInitialLoaded = true)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en getGeneralOrderList: ${e.message}", e)
                throw e
            } finally {
                isFetchingOrders = false
            }
        }
    }

    fun refresh() {
        if (isFetchingOrders) return
        isFetchingOrders = true
        Log.d("AppViewModel", "refresh: Forzando refresco")
        execute(globalUiStateManager = globalUiStateManager) {
            try {
                // Aseguramos tener el área para el refresh
                val currentArea = if (orderUiState.userArea.isNullOrBlank()) {
                    io { dataUseCase.getUserLocal()?.area }
                } else {
                    orderUiState = orderUiState.copy(userArea = orderUiState.userArea) // Trigger state
                    orderUiState.userArea
                }

                val response = if (!currentArea.isNullOrBlank() && currentArea != "0") {
                    dataUseCase.loadParentOrderByBranch(currentArea, forceRefresh = true)
                } else {
                    dataUseCase.loadParentOrder(forceRefresh = true)
                }
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    updateStateWithOrders(response)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en refresh: ${e.message}", e)
                throw e
            } finally {
                isFetchingOrders = false
            }
        }
    }

    private fun updateStateWithOrders(orders: List<ParentOrderModel>) {
        val sortedOrders = orders.sortedBy {
            when (it.state.trim().uppercase()) {
                "CONFIRMADO" -> 1
                "RECEPCIONADO" -> 2
                "LISTO" -> 3
                else -> 4
            }
        }

        val countEntregado = orders.count { it.state.trim().uppercase() == "ENTREGADO" }
        val countPendientes = orders.size - countEntregado

        val targetFilter = if (orderUiState.selectedFilter == "TODOS") "PENDIENTES" else orderUiState.selectedFilter

        val filtered = if (targetFilter == "ENTREGADO") {
            sortedOrders.filter { it.state.trim().uppercase() == "ENTREGADO" }
        } else {
            sortedOrders.filter { it.state.trim().uppercase() != "ENTREGADO" }
        }

        orderUiState = orderUiState.copy(
            orders = sortedOrders,
            filteredOrders = filtered,
            selectedFilter = targetFilter,
            countPendientes = countPendientes,
            countEntregado = countEntregado
        )
    }

    fun applyFilter(filter: String) {
        val filtered = if (filter.uppercase() == "ENTREGADO") {
            orderUiState.orders.filter { it.state.trim().uppercase() == "ENTREGADO" }
        } else {
            orderUiState.orders.filter { it.state.trim().uppercase() != "ENTREGADO" }
        }
        orderUiState = orderUiState.copy(
            filteredOrders = filtered,
            selectedFilter = filter.uppercase()
        )
    }

    fun updateOrderState(order: ParentOrderModel, newState: String, driverId: String? = null) {
        if (order.state.trim().uppercase() == newState.uppercase() && driverId == null) return

        // 1. Guardar estado previo para Reversión (Rollback) en caso de error
        val previousState = orderUiState

        // 2. Actualización Optimista: Actualizamos la UI inmediatamente
        Log.d(
            "AppViewModel",
            "updateOrderState: Actualización optimista de ${order.uid} a $newState (Driver: $driverId)"
        )
        
        val orderWithNewData = if (driverId != null) {
            order.copy(state = newState, driverId = driverId)
        } else {
            order.copy(state = newState)
        }

        val updatedOrders = orderUiState.orders.map {
            if (it.uid == order.uid) orderWithNewData else it
        }
        updateStateWithOrders(updatedOrders)

        // 3. Sincronización en segundo plano
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            try {
                dataUseCase.updateOrder(orderWithNewData)
                Log.d("AppViewModel", "updateOrderState: Sincronización exitosa con servidor")
            } catch (e: Exception) {
                // 4. Rollback: Si falla el servidor, devolvemos la UI a su estado anterior
                Log.e("AppViewModel", "updateOrderState: Error al sincronizar. Revirtiendo UI.", e)
                orderUiState = previousState
                throw e
            }
        }
    }

    fun avanzarEstado(order: ParentOrderModel) {
        val currentState = order.state.trim().uppercase()
        val isDelivery = order.reception.trim().uppercase().contains("DELIVERY")

        val nextState = when (currentState) {
            "CONFIRMADO" -> "RECEPCIONADO"
            "RECEPCIONADO" -> "LISTO"
            "LISTO" -> if (isDelivery) "ENVIADO" else "ENTREGADO"
            else -> null
        }

        nextState?.let {
            if (currentState == "CONFIRMADO") {
                if (orderUiState.isPrinterConnected) {
                    printOrder(order)
                } else {
                    Log.e("AppViewModel", "Intento de impresión omitido: Impresora desconectada")
                }
            }
            updateOrderState(order, it)
        }
    }

    fun printOrder(order: ParentOrderModel) {
        execute(loading = false,globalUiStateManager = globalUiStateManager) {
            try {
                val ticket = TicketFormatter.formatOrder(order)
                printerManager.printTicket(ticket)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error al mandar a imprimir: ${e.message}")
            }
        }
    }

    fun syncProducts(onComplete: (Boolean) -> Unit = {}) {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            try {
                dataUseCase.syncProducts()
                val updatedProducts = dataUseCase.getProducts()
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    orderUiState = orderUiState.copy(
                        products = updatedProducts,
                        pizzaProducts = updatedProducts.filter { it.type == "1" },
                        extraProducts = updatedProducts.filter { it.type == "2" || it.type == "3" },
                        deliveryProducts = updatedProducts.filter { it.type == "4" }
                    )
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error al sincronizar productos en Splash: ${e.message}")
            } finally {
                val localUser = io { dataUseCase.getUserLocal() }
                onComplete(localUser != null)
            }
        }
    }

    fun selectOrder(order: ParentOrderModel?) {
        orderUiState = orderUiState.copy(selectedOrder = order)
    }

    fun setInitialSelectedBranchId(branchId: String) {
        orderUiState = orderUiState.copy(selectedBranchId = branchId)
    }

    fun reconnectPrinter() {
        printerManager.autoDetectAndConnect()
    }

    fun reprintOrder(order: ParentOrderModel) {
        if (!orderUiState.isPrinterConnected) {
            return
        }
        printOrder(order)
    }

    fun resetOrderState() {
        orderUiState = OrderUiState()
    }

    fun logout(onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            io { dataUseCase.logout() }
            prefs.edit().putString("selected_branch_id", "0").apply()
            onSuccess()
        }
    }
}
