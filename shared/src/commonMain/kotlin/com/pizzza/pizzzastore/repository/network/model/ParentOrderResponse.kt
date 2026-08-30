package com.pizzza.pizzzastore.repository.network.model

import com.pizzza.pizzzastore.model.ParentOrderModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.String

@Serializable
class ParentOrderResponse(
    @SerialName("uid")
    val uid: String? = "",
    @SerialName("nameClient")
    val nameClient: String? = "",
    @SerialName("description")
    val description: String? = "",
    @SerialName("priceTotal")
    val price: String? = "",
    @SerialName("phone")
    val phone: String? = "",
    @SerialName("date")
    val date: String? = "",
    @SerialName("state")
    val state: String? = "",
    @SerialName("address")
    val address: String? = "",
    @SerialName("reception")
    val reception: String? = "",
    @SerialName("symbol")
    val symbol: String? = "$",
    @SerialName("branchId")
    val branchId: String? = "1",
    @SerialName("stage")
    val stage: String? = "1",
    @SerialName("latitude")
    val latitude: String? = "0",
    @SerialName("longitude")
    val longitude: String? = "0",
    @SerialName("userId")
    val userId: String? = "0",
    @SerialName("driverId")
    val driverId: String? = "0",
    @SerialName("orders")
    val orders: List<OrderResponse>? = emptyList()
)

fun List<ParentOrderResponse>.loadParentOrder() = this.map {
    ParentOrderModel(
        uid = it.uid ?: "",
        nameClient = it.nameClient ?: "",
        description = it.description ?: "",
        price = it.price ?: "",
        phone = it.phone ?: "",
        date = it.date ?: "",
        state = it.state ?: "",
        address = it.address ?: "",
        reception = it.reception ?: "",
        symbol = it.symbol ?: "$",
        branchId = it.branchId ?: "1",
        stage = it.stage ?: "1",
        latitude = it.latitude ?: "0",
        longitude = it.longitude ?: "0",
        userId = it.userId ?: "0",
        driverId = it.driverId ?: "0",
        orders = it.orders?.loadOrder() ?: emptyList()
    )
}
