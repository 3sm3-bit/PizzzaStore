package com.pizzza.pizzzastore.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("nameUser")
    val nameUser: String? = "",
    @SerialName("names")
    val names: String? = "",
    @SerialName("lastName")
    val lastName: String? = "",
    @SerialName("document")
    val document: String? = "",
    @SerialName("email")
    val email: String? = "",
    @SerialName("password")
    val password: String? = "",
    @SerialName("phone")
    val phone: String? = "",
    @SerialName("address")
    val address: String? = "",
    @SerialName("rol")
    val rol: String? = "CLIENTE",
    @SerialName("area")
    val area: String? = "1",
    @SerialName("longitude")
    val longitude: String? = "",
    @SerialName("latitude")
    val latitude: String? = "",
    @SerialName("uid")
    val uid: String? = ""
)
