package com.pizzza.pizzzastore.model

data class ProductModel(
    val nameProduct: String,
    val type: String,
    val price: String,
    val tamanio: String,
    val description: String,
    val priceChosse: String,
    val currency: String,
    val currencySymbol: String,
    val state: Boolean,
    val uid: String,
    val urlImg: String
)
