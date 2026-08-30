package com.pizzza.pizzzastore.model

data class OrderModel (
    val ui : String,
    val nameClient : String,
    val quantity : String,
    val type : String,
    val symbol : String,
    val nameProduct : String,
    val tamanio : String,
    val typeDough : String,
    val cheeseFilledCrust : String,
    val note : String,
    val phone : String,
    val price : String,
    val priceTotal : String,
    val state : String,
    val date : String,
    val address : String,
    val reception : String,
    val priceDelivery : String,
    val priceChosse : String,
    val idOrden : String,
    val branchId : String,
    val stage : String,
    val userId : String,
    val driverId : String
)