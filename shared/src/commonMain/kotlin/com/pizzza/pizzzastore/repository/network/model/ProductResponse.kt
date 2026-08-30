package com.pizzza.pizzzastore.repository.network.model

import com.pizzza.pizzzastore.model.ProductModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("nameProduct")
    val nameProduct: String? = "",
    @SerialName("type")
    val type: String? = "",
    @SerialName("price")
    val price: String? = "",
    @SerialName("tamanio")
    val tamanio: String? = "",
    @SerialName("description")
    val description: String? = "",
    @SerialName("PriceChosse")
    val priceChosse: String? = "",
    @SerialName("currency")
    val currency: String? = "",
    @SerialName("currencySymbol")
    val currencySymbol: String? = "",
    @SerialName("state")
    val state: Boolean? = true,
    @SerialName("urlImg")
    val urlImg: String? = "",
    @SerialName("uid")
    val uid: String? = ""
)

fun List<ProductResponse>.toModelList() = map {
    ProductModel(
        nameProduct = it.nameProduct ?: "",
        type = it.type ?: "",
        price = it.price ?: "",
        tamanio = it.tamanio ?: "",
        description = it.description ?: "",
        priceChosse = it.priceChosse ?: "",
        currency = it.currency ?: "",
        currencySymbol = it.currencySymbol ?: "",
        state = it.state ?: true,
        urlImg = it.urlImg ?: "",
        uid = it.uid ?: ""
    )
}

fun ProductModel.toResponse() = ProductResponse(
    nameProduct = nameProduct,
    type = type,
    price = price,
    tamanio = tamanio,
    description = description,
    priceChosse = priceChosse,
    currency = currency,
    currencySymbol = currencySymbol,
    state = state,
    urlImg = urlImg,
    uid = uid
)
