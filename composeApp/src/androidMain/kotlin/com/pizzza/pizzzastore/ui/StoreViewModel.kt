package com.pizzza.pizzzastore.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pizzza.pizzzastore.DispatcherProvider
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.ui.base.BaseViewModel
import com.pizzza.pizzzastore.ui.orders.OrderUiState
import com.pizzza.pizzzastore.usecases.DataUseCase
import kotlinx.coroutines.withContext

class StoreViewModel(
    private val dataUseCase: DataUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel(dispatchers) {

    var storeUiState by mutableStateOf(OrderUiState())
        private set

    fun getProductsList() {
        val hasData = storeUiState.products.isNotEmpty()
        execute(loading = !hasData) {
            try {
                val response = dataUseCase.getProducts()
                withContext(dispatchers.main) {
                    storeUiState = storeUiState.copy(
                        products = response,
                        pizzaProducts = response.filter { it.type == "1" },
                        extraProducts = response.filter { it.type == "2" || it.type == "3" },
                        deliveryProducts = response.filter { it.type == "4" }
                    )
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error en getProductsList: ${e.message}", e)
                throw e
            }
        }
    }

    fun getBranchesList() {
        val hasData = storeUiState.branches.isNotEmpty()
        execute(loading = !hasData) {
            try {
                val response = dataUseCase.getBranches()
                withContext(dispatchers.main) {
                    storeUiState = storeUiState.copy(branches = response)
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error en getBranchesList: ${e.message}", e)
                throw e
            }
        }
    }

    fun updateProduct(product: ProductModel, imageBytes: ByteArray? = null, onSuccess: () -> Unit) {
        execute {
            try {
                var productToUpdate = product
                
                // Si hay una nueva imagen, primero la subimos
                if (imageBytes != null) {
                    println("StoreViewModel: Subiendo nueva imagen antes de actualizar producto...")
                    val newUrl = dataUseCase.uploadProductImage(imageBytes)
                    productToUpdate = product.copy(urlImg = newUrl)
                    println("StoreViewModel: Imagen subida con éxito. Nueva URL: $newUrl")
                }

                dataUseCase.updateProduct(productToUpdate)
                getProductsList()
                withContext(dispatchers.main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error al actualizar producto: ${e.message}", e)
                throw e
            }
        }
    }

    fun updateBranch(branch: BranchModel, onSuccess: () -> Unit) {
        execute {
            try {
                dataUseCase.updateBranch(branch)
                getBranchesList()
                withContext(dispatchers.main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error al actualizar sucursal: ${e.message}", e)
                throw e
            }
        }
    }

    fun uploadProductImage(image: ByteArray, onSuccess: (String) -> Unit) {
        execute {
            try {
                val url = dataUseCase.uploadProductImage(image)
                withContext(dispatchers.main) {
                    onSuccess(url)
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Error al subir imagen: ${e.message}", e)
                throw e
            }
        }
    }

    fun selectBranch(branch: BranchModel?) {
        storeUiState = storeUiState.copy(selectedBranch = branch)
    }

    fun selectProduct(product: ProductModel?) {
        storeUiState = storeUiState.copy(selectedProduct = product)
    }

    fun setCategory(category: String) {
        storeUiState = storeUiState.copy(selectedCategory = category)
    }
}
