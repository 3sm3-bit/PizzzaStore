package com.pizzza.pizzzastore.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pizzza.pizzzastore.DispatcherProvider
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.orders.OrderUiState
import com.pizzza.pizzzastore.usecases.DataUseCase
import kotlinx.coroutines.withContext

class AppViewModel(
    private val dataUseCase: DataUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel(dispatchers) {

    var orderUiState by mutableStateOf(OrderUiState())
        private set

    fun getGeneralOrderList() {
        Log.d("AppViewModel", "getGeneralOrderList: Iniciando ejecución")
        execute {
            try {
                val response = dataUseCase.loadParentOrder()
                updateStateWithOrders(response)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en getGeneralOrderList: ${e.message}", e)
                throw e
            }
        }
    }

    fun refresh() {
        Log.d("AppViewModel", "refresh: Forzando refresco")
        execute {
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
                "LISTO" -> 2
                else -> 3
            }
        }

        val countConfirmado = orders.count { it.state.trim().uppercase() == "CONFIRMADO" }
        val countListo = orders.count { it.state.trim().uppercase() == "LISTO" }

        orderUiState = orderUiState.copy(
            orders = sortedOrders,
            filteredOrders = sortedOrders, // Mostramos todos por defecto ya que no hay filtros
            countConfirmado = countConfirmado,
            countListo = countListo
        )
    }

    fun applyFilter(filter: String) {
        val filtered = if (filter == "TODOS") {
            orderUiState.orders
        } else {
            orderUiState.orders.filter { it.state.trim().uppercase() == filter.uppercase() }
        }
        orderUiState = orderUiState.copy(
            filteredOrders = filtered,
            selectedFilter = filter
        )
    }

    fun updateOrderState(order: ParentOrderModel, newState: String) {
        if (order.state.trim().uppercase() == newState.uppercase()) return

        // 1. Guardar estado previo para Reversión (Rollback) en caso de error
        val previousState = orderUiState

        // 2. Actualización Optimista: Actualizamos la UI inmediatamente
        Log.d("AppViewModel", "updateOrderState: Actualización optimista de ${order.uid} a $newState")
        val updatedOrders = orderUiState.orders.map { 
            if (it.uid == order.uid) it.copy(state = newState) else it 
        }
        updateStateWithOrders(updatedOrders)

        // 3. Sincronización en segundo plano
        // Usamos loading = false para que no aparezca el progreso global y la app se sienta "rápida"
        execute(loading = false) {
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
        val nextState = when (currentState) {
            "CONFIRMADO" -> "LISTO"
            "LISTO" -> "ENTREGADO" // O el estado final que manejes
            else -> "CONFIRMADO"
        }
        updateOrderState(order, nextState)
    }

    fun syncProducts(onComplete: (Boolean) -> Unit = {}) {
        execute(loading = false) {
            try {
                println("AppViewModel: Iniciando sincronización obligatoria...")
                dataUseCase.syncProducts()
                // Cargar lo que el servidor acaba de mandar (y que ya está en DB)
                val updatedProducts = dataUseCase.getProducts()
                withContext(dispatchers.main) {
                    orderUiState = orderUiState.copy(
                        products = updatedProducts,
                        pizzaProducts = updatedProducts.filter { it.type == "1" },
                        extraProducts = updatedProducts.filter { it.type == "2" || it.type == "3" },
                        deliveryProducts = updatedProducts.filter { it.type == "4" }
                    )
                    println("AppViewModel: Sincronización exitosa. Total: ${updatedProducts.size}")
                    onComplete(true)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error crítico en sincronización: ${e.message}")
                withContext(dispatchers.main) {
                    onComplete(false)
                }
            }
        }
    }

    fun getProductsList() {
        val hasData = orderUiState.products.isNotEmpty()
        // Si ya hay datos, cargamos en segundo plano para no bloquear
        execute(loading = !hasData) {
            try {
                val response = dataUseCase.getProducts()
                withContext(dispatchers.main) {
                    orderUiState = orderUiState.copy(
                        products = response,
                        pizzaProducts = response.filter { it.type == "1" },
                        extraProducts = response.filter { it.type == "2" || it.type == "3" },
                        deliveryProducts = response.filter { it.type == "4" }
                    )
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en getProductsList: ${e.message}", e)
                throw e
            }
        }
    }

    fun getBranchesList() {
        val hasData = orderUiState.branches.isNotEmpty()
        execute(loading = !hasData) {
            try {
                val response = dataUseCase.getBranches()
                withContext(dispatchers.main) {
                    orderUiState = orderUiState.copy(branches = response)
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en getBranchesList: ${e.message}", e)
                throw e
            }
        }
    }

    fun selectOrder(order: ParentOrderModel?) {
        orderUiState = orderUiState.copy(selectedOrder = order)
    }

    fun selectBranch(branch: BranchModel?) {
        orderUiState = orderUiState.copy(selectedBranch = branch)
    }

    fun selectProduct(product: ProductModel?) {
        orderUiState = orderUiState.copy(selectedProduct = product)
    }

    fun setCategory(category: String) {
        orderUiState = orderUiState.copy(selectedCategory = category)
    }

    fun toggleNotifications() {
        orderUiState = orderUiState.copy(notificationsEnabled = !orderUiState.notificationsEnabled)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        orderUiState = orderUiState.copy(notificationsEnabled = enabled)
    }

    fun updateProduct(product: ProductModel, onSuccess: () -> Unit) {
        execute {
            try {
                dataUseCase.updateProduct(product)
                getProductsList()
                withContext(dispatchers.main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error al actualizar producto: ${e.message}", e)
                throw e
            }
        }
    }

    fun updateBranch(branch: BranchModel, onSuccess: () -> Unit) {
        execute {
            try {
                dataUseCase.updateBranch(branch)
                // Refrescar la lista localmente o desde el servidor
                getBranchesList()
                withContext(dispatchers.main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error al actualizar sucursal: ${e.message}", e)
                throw e
            }
        }
    }
}
