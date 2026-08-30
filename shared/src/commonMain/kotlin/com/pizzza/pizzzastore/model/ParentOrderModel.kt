package com.pizzza.pizzzastore.model

import com.pizzza.pizzzastore.repository.network.model.ParentOrderResponse
import kotlin.String

data class ParentOrderModel(
    val uid: String,
    val nameClient: String,
    val description: String,
    val price: String,
    val phone: String,
    val date: String,
    val state: String,
    val address: String,
    val reception: String,
    val symbol: String,
    val branchId: String,
    val stage: String,
    val latitude: String,
    val longitude: String,
    val userId: String,
    val driverId: String,
    val orders: List<OrderModel>
) {
    fun toParentOrderRequest() =
        ParentOrderResponse(
            uid = uid,
            nameClient = nameClient,
            description = description,
            price = price,
            phone = phone,
            date = date,
            state = state,
            address = address,
            reception = reception,
            symbol = symbol,
            branchId = branchId,
            stage = stage,
            latitude = latitude,
            longitude = longitude,
            userId = userId,
            driverId = driverId,
            orders = emptyList() // Or map it back if needed, but usually for requests we might not need all orders
        )
}