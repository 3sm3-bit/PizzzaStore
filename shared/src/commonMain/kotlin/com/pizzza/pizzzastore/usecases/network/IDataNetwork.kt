package com.pizzza.pizzzastore.usecases.network

import com.pizzza.pizzzastore.model.OrderModel
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.repository.db.entity.UserEntity
import com.pizzza.pizzzastore.repository.network.model.UserResponse

interface IDataNetwork {

    suspend fun loadOrder(): List<OrderModel>

    suspend fun updateOrder(data: ParentOrderModel): String

    suspend fun loadParentOrder(forceRefresh: Boolean = false): List<ParentOrderModel>

    suspend fun loadParentOrderByBranch(branchId: String, forceRefresh: Boolean = false): List<ParentOrderModel>

    suspend fun syncProducts(): List<ProductModel>

    suspend fun getProducts(): List<ProductModel>

    suspend fun updateProduct(data: ProductModel): String

    suspend fun addProduct(data: ProductModel): String

    suspend fun deleteProduct(id: String): String

    suspend fun uploadProductImage(image: ByteArray): String

    suspend fun getBranches(): List<BranchModel>

    suspend fun updateBranch(data: BranchModel): String

    suspend fun addBranch(data: BranchModel): String

    suspend fun getUsers(): List<UserResponse>

    suspend fun getUsersByBranch(branchId: String): List<UserResponse>

    suspend fun deleteUser(id: String): String

    suspend fun registerUser(data: UserResponse): String

    suspend fun updateUser(data: UserResponse): String

    suspend fun login(data: com.pizzza.pizzzastore.repository.network.model.LoginRequest): com.pizzza.pizzzastore.repository.network.model.LoginResponse

    suspend fun saveUserLocal(user: UserEntity)

    suspend fun getUserLocal(): UserEntity?

    suspend fun logout()

}
