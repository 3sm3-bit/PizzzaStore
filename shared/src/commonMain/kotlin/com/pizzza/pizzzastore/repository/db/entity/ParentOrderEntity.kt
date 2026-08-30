package com.pizzza.pizzzastore.repository.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_orders")
data class ParentOrderEntity(
    @PrimaryKey val uid: String,
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
    val driverId: String
)