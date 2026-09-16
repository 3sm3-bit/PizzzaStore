package com.pizzza.pizzzastore.ui

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

class AppViewModel(
    private val dataUseCase: DataUseCase,
    private val printerManager: BluetoothPrinterManager,
    private val webSocketManager: WebSocketManager,
    private val globalUiStateManager: GlobalUiStateManager,
) : BaseViewModel() {

    var orderUiState by mutableStateOf(OrderUiState())
        private set

    init {
        printerManager.autoDetectAndConnect()
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

        // Cambiar a corrutina asíncrona segura
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val user = dataUseCase.getUserLocal()
            orderUiState = orderUiState.copy(userRole = user?.rol)
            Log.d("AppViewModel", "🍕 Rol de usuario cargado: ${user?.rol}")
        }
    }

    fun getGeneralOrderList() {
        if (orderUiState.isInitialLoaded) return
        
        // Verificación de seguridad adicional: no llamar a la API si no hay sesión iniciada en la app
        if (orderUiState.userRole == null) {
            viewModelScope.launch {
                val user = dataUseCase.getUserLocal()
                if (user == null) {
                    Log.d("AppViewModel", "getGeneralOrderList omitido: Sin sesión en base de datos local")
                    return@launch
                } else {
                    orderUiState = orderUiState.copy(userRole = user.rol)
                    proceedToLoadOrders()
                }
            }
        } else {
            proceedToLoadOrders()
        }
    }

    private fun proceedToLoadOrders() {
        Log.d("AppViewModel", "getGeneralOrderList: Iniciando ejecución")
        execute(globalUiStateManager = globalUiStateManager) {
            try {
                val response = dataUseCase.loadParentOrder()
                updateStateWithOrders(response)
                orderUiState = orderUiState.copy(isInitialLoaded = true)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en getGeneralOrderList: ${e.message}", e)
                throw e
            }
        }
    }

    fun refresh() {
        Log.d("AppViewModel", "refresh: Forzando refresco")
        execute(globalUiStateManager = globalUiStateManager) {
            try {
                val response = dataUseCase.loadParentOrder(forceRefresh = true)
                updateStateWithOrders(response)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en refresh: ${e.message}", e)
                throw e
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

        // Mantener o aplicar el filtro actual ("PENDIENTES" por defecto si es TODOS o vacío)
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

    fun updateOrderState(order: ParentOrderModel, newState: String) {
        if (order.state.trim().uppercase() == newState.uppercase()) return

        // 1. Guardar estado previo para Reversión (Rollback) en caso de error
        val previousState = orderUiState

        // 2. Actualización Optimista: Actualizamos la UI inmediatamente
        Log.d(
            "AppViewModel",
            "updateOrderState: Actualización optimista de ${order.uid} a $newState"
        )
        val updatedOrders = orderUiState.orders.map {
            if (it.uid == order.uid) it.copy(state = newState) else it
        }
        updateStateWithOrders(updatedOrders)

        // 3. Sincronización en segundo plano
        // Usamos loading = false para que no aparezca el progreso global y la app se sienta "rápida"
        execute(loading = false,globalUiStateManager = globalUiStateManager) {
            try {
                dataUseCase.updateOrder(order.copy(state = newState))
                Log.d("AppViewModel", "updateOrderState: Sincronización exitosa con servidor")
            } catch (e: Exception) {
                // 4. Rollback: Si falla el servidor, devolvemos la UI a su estado anterior
                Log.e("AppViewModel", "updateOrderState: Error al sincronizar. Revirtiendo UI.", e)
                orderUiState = previousState
                throw e // Permitimos que BaseViewModel muestre el diálogo de error
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
        execute(loading = false,globalUiStateManager = globalUiStateManager) {
            try {
                dataUseCase.syncProducts()
                val updatedProducts = dataUseCase.getProducts()
                orderUiState = orderUiState.copy(
                    products = updatedProducts,
                    pizzaProducts = updatedProducts.filter { it.type == "1" },
                    extraProducts = updatedProducts.filter { it.type == "2" || it.type == "3" },
                    deliveryProducts = updatedProducts.filter { it.type == "4" }
                )
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error al sincronizar productos en Splash: ${e.message}")
            } finally {
                val localUser = io { dataUseCase.getUserLocal() }
                println("AppViewModel: Sincronización finalizada. Total: ${orderUiState.products.size}")
                onComplete(localUser != null)
            }
        }
    }

    fun selectOrder(order: ParentOrderModel?) {
        orderUiState = orderUiState.copy(selectedOrder = order)
    }

    fun updateSelectedBranchForNotifications(branchId: String) {
        orderUiState = orderUiState.copy(selectedBranchId = branchId)
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
            onSuccess()
        }
    }
}
