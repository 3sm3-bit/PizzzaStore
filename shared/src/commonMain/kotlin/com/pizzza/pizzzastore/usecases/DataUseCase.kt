package com.pizzza.pizzzastore.usecases

import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.repository.network.model.UserResponse
import com.pizzza.pizzzastore.usecases.network.IDataNetwork


class DataUseCase(private val iDataNetwork: IDataNetwork) {

    suspend fun loadOrder() = iDataNetwork.loadOrder()

    suspend fun loadParentOrder(forceRefresh: Boolean = false) = iDataNetwork.loadParentOrder(forceRefresh)

    suspend fun loadParentOrderByBranch(branchId: String, forceRefresh: Boolean = false) = iDataNetwork.loadParentOrderByBranch(branchId, forceRefresh)

    suspend fun updateOrder(data: ParentOrderModel) = iDataNetwork.updateOrder(data)

    suspend fun syncProducts() = iDataNetwork.syncProducts()

    suspend fun getProducts() = iDataNetwork.getProducts()

    suspend fun updateProduct(data: ProductModel) = iDataNetwork.updateProduct(data)

    suspend fun addProduct(data: ProductModel) = iDataNetwork.addProduct(data)

    suspend fun deleteProduct(id: String) = iDataNetwork.deleteProduct(id)

    suspend fun uploadProductImage(image: ByteArray) = iDataNetwork.uploadProductImage(image)

    suspend fun getBranches() = iDataNetwork.getBranches()

    suspend fun updateBranch(data: BranchModel) = iDataNetwork.updateBranch(data)

    suspend fun addBranch(data: BranchModel) = iDataNetwork.addBranch(data)

    suspend fun getUsers() = iDataNetwork.getUsers()

    suspend fun getUsersByBranch(branchId: String) = iDataNetwork.getUsersByBranch(branchId)

    suspend fun deleteUser(id: String) = iDataNetwork.deleteUser(id)

    suspend fun registerUser(data: UserResponse) = iDataNetwork.registerUser(data)

    suspend fun updateUser(data: UserResponse) = iDataNetwork.updateUser(data)

    suspend fun login(data: com.pizzza.pizzzastore.repository.network.model.LoginRequest) = iDataNetwork.login(data)

    suspend fun saveUserLocal(user: com.pizzza.pizzzastore.repository.db.entity.UserEntity) = iDataNetwork.saveUserLocal(user)

    suspend fun getUserLocal() = iDataNetwork.getUserLocal()

    suspend fun logout() {
        iDataNetwork.logout()
    }

}