package com.pizzza.pizzzastore.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.base.GlobalUiStateManager
import com.pizzza.pizzzastore.ui.orders.OrderUiState
import com.pizzza.pizzzastore.usecases.DataUseCase
import kotlinx.coroutines.CoroutineDispatcher

class StoreViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
) : BaseViewModel() {

    var storeUiState by mutableStateOf(OrderUiState())
        private set

    fun getProductsList() {
        val hasData = storeUiState.products.isNotEmpty()
        execute(loading = !hasData, globalUiStateManager = globalUiStateManager) {
            fetchProducts()
        }
    }

    suspend fun fetchProducts() {
        val response = dataUseCase.syncProducts()
        storeUiState = storeUiState.copy(
            products = response,
            pizzaProducts = response.filter { it.type == "1" },
            extraProducts = response.filter { it.type == "2" || it.type == "3" },
            deliveryProducts = response.filter { it.type == "4" }
        )
    }

    fun getBranchesList() {
        val hasData = storeUiState.branches.isNotEmpty()
        execute(loading = !hasData, globalUiStateManager = globalUiStateManager) {
            fetchBranches()
        }
    }

    suspend fun fetchBranches() {
        val response = dataUseCase.getBranches()
        storeUiState = storeUiState.copy(branches = response)
    }

    fun updateProduct(product: ProductModel, imageBytes: ByteArray? = null, onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {

            var productToUpdate = product

            // Si hay una nueva imagen, primero la subimos
            if (imageBytes != null) {
                println("StoreViewModel: Subiendo nueva imagen antes de actualizar producto...")
                val newUrl = dataUseCase.uploadProductImage(imageBytes)
                productToUpdate = product.copy(urlImg = newUrl)
                println("StoreViewModel: Imagen subida con éxito. Nueva URL: $newUrl")
            }

            dataUseCase.updateProduct(productToUpdate)
            fetchProducts()
            onSuccess()

        }
    }

    fun deleteProduct(id: String, onSuccess: () -> Unit) {
        execute(loading = true, globalUiStateManager = globalUiStateManager) {
            dataUseCase.deleteProduct(id)
            fetchProducts()
            onSuccess()
        }
    }

    fun createProduct(product: ProductModel, imageBytes: ByteArray? = null, onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            var productToCreate = product

            if (imageBytes != null) {
                println("StoreViewModel: Subiendo imagen para nuevo producto...")
                val newUrl = dataUseCase.uploadProductImage(imageBytes)
                productToCreate = product.copy(urlImg = newUrl)
            }

            dataUseCase.addProduct(productToCreate)
            fetchProducts()
            onSuccess()
        }
    }

    fun updateBranch(branch: BranchModel, onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            dataUseCase.updateBranch(branch)
            fetchBranches()
            onSuccess()

        }
    }

    fun createBranch(branch: BranchModel, onSuccess: () -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            dataUseCase.addBranch(branch)
            fetchBranches()
            onSuccess()
        }
    }

    fun uploadProductImage(image: ByteArray, onSuccess: (String) -> Unit) {
        execute {
                val url = dataUseCase.uploadProductImage(image)
                    onSuccess(url)
        }
    }

    fun selectBranch(branch: BranchModel?) {
        storeUiState = storeUiState.copy(selectedBranch = branch)
    }

    fun getUsersList() {
        execute(loading = true, globalUiStateManager = globalUiStateManager) {
            fetchUsers()
        }
    }

    suspend fun fetchUsers() {
        val response = dataUseCase.getUsers()

        storeUiState = storeUiState.copy(
            users = response,
            filteredUsers = filterUsers(response, storeUiState.userFilter)
        )
    }

    fun deleteUser(id: String, onSuccess: () -> Unit) {
        execute(loading = true, globalUiStateManager = globalUiStateManager) {
            dataUseCase.deleteUser(id)
            fetchUsers()
            onSuccess()
        }
    }

    fun setUserFilter(filter: String) {
        storeUiState = storeUiState.copy(
            userFilter = filter,
            filteredUsers = filterUsers(storeUiState.users, filter)
        )
    }

    private fun filterUsers(users: List<com.pizzza.pizzzastore.repository.network.model.UserResponse>, filter: String): List<com.pizzza.pizzzastore.repository.network.model.UserResponse> {
        return if (filter == "CLIENTE") {
            users.filter { it.rol == "CLIENTE" }
        } else {
            users.filter { it.rol != "CLIENTE" }
        }
    }

    fun selectProduct(product: ProductModel?) {
        storeUiState = storeUiState.copy(selectedProduct = product)
    }

    fun setCategory(category: String) {
        storeUiState = storeUiState.copy(selectedCategory = category)
    }
}
