package com.pizzza.pizzzastore.ui.orders

import com.pizzza.pizzzastore.model.ParentOrderModel
import com.pizzza.pizzzastore.model.ProductModel
import com.pizzza.pizzzastore.model.BranchModel

data class OrderUiState(
    val orders: List<ParentOrderModel> = emptyList(),
    val filteredOrders: List<ParentOrderModel> = emptyList(),
    val selectedFilter: String = "TODOS",
    val countConfirmado: Int = 0,
    val countListo: Int = 0,
    val selectedOrder: ParentOrderModel? = null,
    val products: List<ProductModel> = emptyList(),
    val selectedProduct: ProductModel? = null,
    val branches: List<BranchModel> = emptyList(),
    val selectedBranch: BranchModel? = null,
    val selectedBranchId: String = "1",
    val selectedCategory: String = "Pizza",
    val pizzaProducts: List<ProductModel> = emptyList(),
    val extraProducts: List<ProductModel> = emptyList(),
    val deliveryProducts: List<ProductModel> = emptyList()
)
