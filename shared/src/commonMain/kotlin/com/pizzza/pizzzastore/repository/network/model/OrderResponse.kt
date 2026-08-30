package com.pizzza.pizzzastore.repository.network.model

import com.pizzza.pizzzastore.model.OrderModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OrderResponse (
    @SerialName("uid")
    val uid : String? = "",
    @SerialName("nameClient")
    val nameClient : String? = "",
    @SerialName("quantity")
    val quantity : String? = "",
    @SerialName("type")
    val type : String? = "1",
    @SerialName("symbol")
    val symbol : String? = "$",
    @SerialName("nameProduct")
    val nameProduct : String? = "",
    @SerialName("tamanio")
    val tamanio : String? = "",
    @SerialName("typeDough")
    val typeDough : String? = "TRADICIONAL",
    @SerialName("cheeseFilledCrust")
    val cheeseFilledCrust : String? = "NO",
    @SerialName("note")
    val note : String? = "",
    @SerialName("phone")
    val phone : String? = "",
    @SerialName("price")
    val price : String? = "",
    @SerialName("priceTotal")
    val priceTotal : String? = "0",
    @SerialName("state")
    val state : String? = "PENDIENTE",
    @SerialName("date")
    val date : String? = "",
    @SerialName("address")
    val address: String? = "",
    @SerialName("reception")
    val reception: String? = "",
    @SerialName("priceDelivery")
    val priceDelivery: String? = "0",
    @SerialName("priceChosse")
    val priceChosse: String? = "0",
    @SerialName("idOrden")
    val idOrden: String? = "",
    @SerialName("branchId")
    val branchId: String? = "1",
    @SerialName("stage")
    val stage: String? = "1",
    @SerialName("userId")
    val userId: String? = "0",
    @SerialName("driverId")
    val driverId: String? = "0"
)

fun List<OrderResponse>.loadOrder() = this.map {
    OrderModel(
        ui = it.uid ?: "",
        nameClient = it.nameClient ?: "",
        quantity = it.quantity ?: "",
        type = it.type ?: "1",
        symbol = it.symbol ?: "$",
        nameProduct = it.nameProduct ?: "",
        tamanio = it.tamanio ?: "",
        typeDough = it.typeDough ?: "TRADICIONAL",
        cheeseFilledCrust = it.cheeseFilledCrust ?: "NO",
        note = it.note ?: "",
        phone = it.phone ?: "",
        price = it.price ?: "",
        priceTotal = it.priceTotal ?: "0",
        state = it.state ?: "PENDIENTE",
        date = it.date ?: "",
        address = it.address ?: "",
        reception = it.reception ?: "",
        priceDelivery = it.priceDelivery ?: "0",
        priceChosse = it.priceChosse ?: "0",
        idOrden = it.idOrden ?: "",
        branchId = it.branchId ?: "1",
        stage = it.stage ?: "1",
        userId = it.userId ?: "0",
        driverId = it.driverId ?: "0"
    )
}
