package com.pizzza.pizzzastore.repository.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val uid: String,
    val nameUser: String,
    val names: String,
    val lastName: String,
    val document: String,
    val email: String,
    val phone: String,
    val address: String,
    val rol: String,
    val area: String,
    val longitude: String,
    val latitude: String,
    val token: String
)
