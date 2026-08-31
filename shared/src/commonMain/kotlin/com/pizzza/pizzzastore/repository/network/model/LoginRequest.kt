package com.pizzza.pizzzastore.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("nameUser")
    val nameUser: String,
    @SerialName("password")
    val password: String
)
