package com.pizzza.pizzzastore.usecases.network

import com.pizzza.pizzzastore.model.OrderModel
import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.model.BranchModel
import com.pizzza.pizzzastore.repository.network.model.UserResponse

interface IDataNetwork {

    suspend fun loadOrder(): List<OrderModel>

    suspend fun updateOrder(data: ParentOrderModel): String

    suspend fun loadParentOrder(forceRefresh: Boolean = false): List<ParentOrderModel>

    suspend fun syncProducts(): List<ProductModel>

    suspend fun getProducts(): List<ProductModel>

    suspend fun updateProduct(data: ProductModel): String

    suspend fun getBranches(): List<BranchModel>

    suspend fun updateBranch(data: BranchModel): String

    suspend fun registerUser(data: UserResponse): String

    suspend fun login(data: com.pizzza.pizzzastore.repository.network.model.LoginRequest): com.pizzza.pizzzastore.repository.network.model.LoginResponse

    suspend fun saveUserLocal(user: com.pizzza.pizzzastore.repository.db.entity.UserEntity)

    suspend fun getUserLocal(): com.pizzza.pizzzastore.repository.db.entity.UserEntity?

    suspend fun logout()

}
