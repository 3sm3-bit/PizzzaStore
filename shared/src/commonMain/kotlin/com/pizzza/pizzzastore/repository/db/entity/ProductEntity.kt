package com.pizzza.pizzzastore.repository.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val uid: String,
    val nameProduct: String,
    val type: String,
    val price: String,
    val tamanio: String,
    val description: String,
    val priceChosse: String,
    val currency: String,
    val currencySymbol: String,
    val state: Boolean,
    val urlImg: String = ""
)